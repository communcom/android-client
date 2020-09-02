package io.golos.data.repositories.discussion

import io.golos.commun4j.Commun4j
import io.golos.commun4j.abi.implementation.c.gallery.MssgidCGalleryStruct
import io.golos.commun4j.model.BandWidthRequest
import io.golos.commun4j.model.ClientAuthRequest
import io.golos.commun4j.model.FeedTimeFrame
import io.golos.commun4j.model.FeedType
import io.golos.commun4j.services.model.CommentsSortBy
import io.golos.commun4j.services.model.DonationPostModel
import io.golos.commun4j.services.model.UserAndPermlinkPair
import io.golos.commun4j.sharedmodel.CyberName
import io.golos.commun4j.sharedmodel.CyberSymbolCode
import io.golos.data.mappers.*
import io.golos.data.repositories.network_call.NetworkCallProxy
import io.golos.data.toCyberName
import io.golos.domain.DispatchersProvider
import io.golos.domain.UserKeyStore
import io.golos.domain.dto.*
import io.golos.domain.posts_parsing_rendering.mappers.json_to_dto.JsonToDtoMapper
import io.golos.domain.posts_parsing_rendering.mappers.json_to_dto.mappers.MappersFactory
import io.golos.domain.posts_parsing_rendering.mappers.json_to_dto.mappers.PostMapper
import io.golos.domain.repositories.CurrentUserRepositoryRead
import io.golos.domain.repositories.DiscussionRepository
import io.golos.utils.format.DatesServerFormatter
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import org.json.JSONObject
import timber.log.Timber
import java.io.File
import java.lang.Exception
import java.util.*
import javax.inject.Inject

class DiscussionRepositoryImpl
@Inject
constructor(
    private val callProxy: NetworkCallProxy,
    private val dispatchersProvider: DispatchersProvider,
    private val currentUserRepository: CurrentUserRepositoryRead,
    private val commun4j: Commun4j,
    private val userKeyStore: UserKeyStore
) : DiscussionRepository {

    override suspend fun updatePost(contentIdDomain: ContentIdDomain, body: String, tags: List<String>): PostDomain {
        val oldPost = getPost(contentIdDomain.userId.toCyberName(), contentIdDomain.communityId, contentIdDomain.permlink)

        val updatePostResult = callProxy.callBC {
            commun4j.updatePostOrComment(
                messageId = MssgidCGalleryStruct(contentIdDomain.userId.toCyberName(), contentIdDomain.permlink),
                communCode = CyberSymbolCode(contentIdDomain.communityId.code),
                header = "",
                body = body,
                tags = tags,
                metadata = DatesServerFormatter.formatToServer(oldPost.meta.creationTime),
                bandWidthRequest = BandWidthRequest.bandWidthFromComn,
                clientAuthRequest = ClientAuthRequest.empty,
                author = currentUserRepository.userId.userId.toCyberName(),
                authorKey = userKeyStore.getKey(UserKeyType.ACTIVE)
            )
        }

        callProxy.call {
            commun4j.waitForTransaction(updatePostResult.transaction_id)
        }

        return getPost(contentIdDomain.userId.toCyberName(), contentIdDomain.communityId, contentIdDomain.permlink)
    }

    override suspend fun createPost(communityId: CommunityIdDomain, body: String, tags: List<String>): ContentIdDomain {
        val createPostResult = callProxy.callBC {
            commun4j.createPost(
                communCode = CyberSymbolCode(communityId.code),
                header = "",
                body = body,
                tags = listOf(),
                metadata = DatesServerFormatter.formatToServer(Date()),
                weight = null,
                bandWidthRequest = BandWidthRequest.bandWidthFromComn,
                clientAuthRequest = ClientAuthRequest.empty,
                author = currentUserRepository.userId.userId.toCyberName(),
                authorKey = userKeyStore.getKey(UserKeyType.ACTIVE)
            )
        }

        callProxy.call {
            commun4j.waitForTransaction(createPostResult.transaction_id)
        }

        return  with(createPostResult.resolvedResponse!!) {
            ContentIdDomain(
                communityId,
                getMessageId.getPermlink,
                getMessageId.getAuthor.mapToUserIdDomain()
            )
        }
    }

    override suspend fun uploadContentAttachment(file: File): String {
        return callProxy.callBC { commun4j.uploadImage(file) }
    }

    @Suppress("UNCHECKED_CAST")
    override suspend fun getPosts(
        postsConfigurationDomain: PostsConfigurationDomain,
        typeObject: TypeObjectDomain
    ): List<PostDomain> = coroutineScope {
        val type = getFeedType(postsConfigurationDomain.typeFeed, typeObject)
        val timeFrame = getFeedTimeFrame(postsConfigurationDomain.timeFrame, postsConfigurationDomain.typeFeed)

        val posts = callProxy.call {
            commun4j.getPostsRaw(
                if (typeObject != TypeObjectDomain.COMMUNITY) postsConfigurationDomain.userId.toCyberName() else null,
                postsConfigurationDomain.communityId?.code,
                null,
                postsConfigurationDomain.allowNsfw,
                type,
                null,
                timeFrame,
                postsConfigurationDomain.limit,
                postsConfigurationDomain.offset,
                arrayListOf("all")
            )
        }

        if(posts.isNotEmpty()) {
            val contentIds = posts.map { UserAndPermlinkPair(it.contentId.userId, it.contentId.permlink) }
            val donationQuery = posts.map { DonationPostModel(it.contentId.userId.name, it.contentId.permlink) }

            val rewardsAsync = try {
                callProxy
                    .call { commun4j.getStateBulk(contentIds) }
                    .flatMap { it.value }
                    .map { it.mapToRewardPostDomain() }
            } catch (e:Exception){
                null
            }

            val donationsAsync = try {
                callProxy.call { commun4j.getDonations(donationQuery) }.items.map { it.mapToDonationsDomain() }
            } catch (e:Exception){
                null
            }
            val rewards = rewardsAsync as? Iterable<RewardPostDomain>
            val donations = donationsAsync as? Iterable<DonationsDomain>

            withContext(dispatchersProvider.calculationsDispatcher) {
                posts.items.map { post ->
                    val userId = post.author.userId.name

                    val reward = rewards?.firstOrNull {
                        it.contentId.userId.userId == post.contentId.userId.name &&
                                it.contentId.permlink == post.contentId.permlink }

                    val donation = donations?.firstOrNull {
                        it.contentId.userId.userId == post.contentId.userId.name &&
                                it.contentId.permlink == post.contentId.permlink
                    }

                    post.mapToPostDomain(userId == currentUserRepository.userId.userId, reward, donation)
                }
            }
        } else {
            listOf()
        }
    }

    private fun getFeedTimeFrame(
        timeFrame: PostsConfigurationDomain.TimeFrameDomain,
        localFeedType: PostsConfigurationDomain.TypeFeedDomain
    ): FeedTimeFrame? {
        val currentTimeFrame = if (localFeedType == PostsConfigurationDomain.TypeFeedDomain.POPULAR) timeFrame else null
        return if (currentTimeFrame == null) {
            return null
        } else {
            FeedTimeFrame.valueOf(currentTimeFrame.name)
        }
    }

    private fun getFeedType(localFeedType: PostsConfigurationDomain.TypeFeedDomain, typeObject: TypeObjectDomain): FeedType {
        return when (localFeedType) {
            PostsConfigurationDomain.TypeFeedDomain.NEW -> getNewFeedTypeForObject(typeObject)
            PostsConfigurationDomain.TypeFeedDomain.HOT -> getHotForObject(typeObject)
            PostsConfigurationDomain.TypeFeedDomain.POPULAR -> getPopularForObject(typeObject)
            PostsConfigurationDomain.TypeFeedDomain.VOTED -> FeedType.VOTED
        }
    }


    private fun getNewFeedTypeForObject(typeObject: TypeObjectDomain): FeedType {
        return when (typeObject) {
            TypeObjectDomain.USER -> FeedType.BY_USER
            TypeObjectDomain.COMMUNITY -> FeedType.COMMUNITY
            TypeObjectDomain.TRENDING -> FeedType.NEW
            TypeObjectDomain.MY_FEED -> FeedType.SUBSCRIPTION
        }
    }

    private fun getHotForObject(typeObject: TypeObjectDomain): FeedType {
        return when (typeObject) {
            TypeObjectDomain.USER -> FeedType.BY_USER
            TypeObjectDomain.COMMUNITY -> FeedType.HOT
            TypeObjectDomain.TRENDING -> FeedType.HOT
            TypeObjectDomain.MY_FEED -> FeedType.SUBSCRIPTION_HOT
        }
    }

    private fun getPopularForObject(typeObject: TypeObjectDomain): FeedType {
        return when (typeObject) {
            TypeObjectDomain.USER -> FeedType.BY_USER
            TypeObjectDomain.COMMUNITY -> FeedType.TOP_LIKES
            TypeObjectDomain.TRENDING -> FeedType.TOP_LIKES
            TypeObjectDomain.MY_FEED -> FeedType.SUBSCRIPTION_POPULAR
        }
    }

    override suspend fun getComments(
        offset: Int,
        pageSize: Int,
        commentType: CommentDomain.CommentTypeDomain,
        userId: UserIdDomain,
        postPermlink: String?,
        communityId: CommunityIdDomain?,
        communityAlias: String?,
        parentComment: ParentCommentIdentifierDomain?
    ): List<CommentDomain> {
        val currentUserId = currentUserRepository.userId.userId
        //        val comments = callProxy.call {
        //            commun4j.getCommentsRaw(
        //                sortBy = CommentsSortBy.TIME_DESC,
        //                offset = offset,
        //                limit = pageSize,
        //                type = commentType.mapToCommentSortType(),
        //                userId = userId.mapToCyberName(),
        //                permlink = postPermlink,
        //                communityId = communityId?.code,
        //                communityAlias = communityAlias,
        //                parentComment = parentComment?.mapToParentComment()
        //            )
        //        }
        //        .items
        //        .map { it.mapToCommentDomain(it.author.userId.name == currentUserId) }


        val comments = callProxy.call {
            commun4j.getCommentsRaw(
                sortBy = CommentsSortBy.TIME_DESC,
                offset = offset,
                limit = pageSize,
                type = commentType.mapToCommentSortType(),
                userId = userId.mapToCyberName(),
                permlink = postPermlink,
                communityId = communityId?.code,
                communityAlias = communityAlias,
                parentComment = parentComment?.mapToParentComment()
            )
        }
            .items

        return if(comments.isNotEmpty()) {
            val donationQuery = comments.map { DonationPostModel(it.contentId.userId.name, it.contentId.permlink) }
            val donations = callProxy.call { commun4j.getDonations(donationQuery) }.items.map { it.mapToDonationsDomain() }

            withContext(dispatchersProvider.calculationsDispatcher) {
                comments.map { comment ->
                    val donation = donations.firstOrNull {
                        it.contentId.userId.userId == comment.contentId.userId.name &&
                                it.contentId.permlink == comment.contentId.permlink
                    }

                    comment.mapToCommentDomain(comment.author.userId.name == currentUserId, donation)
                }
            }
        } else {
            listOf()
        }
        //        .map { it.mapToCommentDomain(it.author.userId.name == currentUserId) }

        //        return comments
    }

    override suspend fun deletePost(permlink: String, communityId: CommunityIdDomain) {
        callProxy.callBC {
            commun4j.deletePostOrComment(
                messageId = MssgidCGalleryStruct(currentUserRepository.userId.mapToCyberName(), permlink),
                communCode = CyberSymbolCode(communityId.code),
                bandWidthRequest = BandWidthRequest.bandWidthFromComn,
                clientAuthRequest = ClientAuthRequest.empty,
                author = currentUserRepository.userId.mapToCyberName(),
                authorKey = userKeyStore.getKey(UserKeyType.ACTIVE)
            )
        }
    }

    override suspend fun deleteComment(permlink: String, communityId: CommunityIdDomain) {
        callProxy.callBC {
            commun4j.deletePostOrComment(
                messageId = MssgidCGalleryStruct(currentUserRepository.userId.mapToCyberName(), permlink),
                communCode = CyberSymbolCode(communityId.code),
                bandWidthRequest = BandWidthRequest.bandWidthFromComn,
                clientAuthRequest = ClientAuthRequest.empty,
                author = currentUserRepository.userId.mapToCyberName(),
                authorKey = userKeyStore.getKey(UserKeyType.ACTIVE)
            )
        }
    }

    override suspend fun updateComment(commentDomain: CommentDomain) {
        val jsonBody = commentDomain.jsonBody!!
        val contentId = commentDomain.contentId
        callProxy.callBC {
            commun4j.updatePostOrComment(
                messageId = MssgidCGalleryStruct(contentId.userId.toCyberName(), contentId.permlink),
                communCode = CyberSymbolCode(contentId.communityId.code),
                header = "",
                body = jsonBody,
                tags = listOf(),
                metadata = DatesServerFormatter.formatToServer(commentDomain.meta.creationTime),
                bandWidthRequest = BandWidthRequest.bandWidthFromComn,
                clientAuthRequest = ClientAuthRequest.empty,
                author = commentDomain.author.userId.toCyberName(),
                authorKey = userKeyStore.getKey(UserKeyType.ACTIVE))
        }
    }

    override suspend fun reportPost(communityId: CommunityIdDomain, authorId: UserIdDomain, permlink: String, reason: String) {
        Timber.tag("NET_SOCKET").d("DiscussionRepositoryImpl::reportPost(communityId: $communityId, authorId: $authorId, permlink: $permlink, reason: $reason)")
        callProxy.callBC {
            commun4j.reportContent(
                CyberSymbolCode(communityId.code),
                messageId = MssgidCGalleryStruct(authorId.toCyberName(), permlink),
                reason = reason,
                bandWidthRequest = BandWidthRequest.bandWidthFromComn,
                clientAuthRequest = ClientAuthRequest.empty,
                key = userKeyStore.getKey(UserKeyType.ACTIVE),
                reporter = CyberName(currentUserRepository.userId.userId)
            )
        }
    }

    override suspend fun upVote(contentIdDomain: ContentIdDomain) {
        val currentUser = currentUserRepository.userId.userId.toCyberName()
        callProxy.callBC {
            commun4j.upVote(
                communCode = CyberSymbolCode(contentIdDomain.communityId.code),
                messageId = MssgidCGalleryStruct(contentIdDomain.userId.toCyberName(), contentIdDomain.permlink),
                weight = 0,
                bandWidthRequest = BandWidthRequest.bandWidthFromComn,
                clientAuthRequest = ClientAuthRequest.empty,
                voter = currentUser,
                key = userKeyStore.getKey(UserKeyType.ACTIVE)
            )
        }
    }

    override suspend fun downVote(contentIdDomain: ContentIdDomain) {
        val currentUser = currentUserRepository.userId.userId.toCyberName()
        callProxy.callBC {
            commun4j.downVote(
                communCode = CyberSymbolCode(contentIdDomain.communityId.code),
                messageId = MssgidCGalleryStruct(contentIdDomain.userId.toCyberName(), contentIdDomain.permlink),
                weight = 0,
                bandWidthRequest = BandWidthRequest.bandWidthFromComn,
                clientAuthRequest = ClientAuthRequest.empty,
                voter = currentUser,
                key = userKeyStore.getKey(UserKeyType.ACTIVE)
            )
        }
    }

    override suspend fun getPost(user: CyberName, communityId: CommunityIdDomain, permlink: String): PostDomain = coroutineScope {
        val post = callProxy.call { commun4j.getPostRaw(user, communityId.code, permlink) }

        val contentIds = listOf(UserAndPermlinkPair(post.contentId.userId, post.contentId.permlink))
        val donationQuery = listOf(DonationPostModel(post.contentId.userId.name, post.contentId.permlink))

        val donations = try {
            callProxy.call { commun4j.getDonations(donationQuery) }.items.firstOrNull()?.mapToDonationsDomain()
        }catch (e:Exception){
            null
        }

        val rewards = try {
            callProxy
                .call { commun4j.getStateBulk(contentIds) }
                .flatMap { it.value }
                .map { it.mapToRewardPostDomain() }
                .firstOrNull()
        }catch (e:Exception){
            null
        }

        post.mapToPostDomain(currentUserRepository.userId.userId, rewards, donations)
    }

    override suspend fun sendComment(postIdDomain: ContentIdDomain, jsonBody: String): CommentDomain {
        val author = currentUserRepository.userId.mapToCyberName()
        val response = callProxy.callBC {
            val metadata = DatesServerFormatter.formatToServer(Date())
            commun4j.createComment(
                parentMssgId = MssgidCGalleryStruct(postIdDomain.userId.toCyberName(), postIdDomain.permlink),
                communCode = CyberSymbolCode(postIdDomain.communityId.code),
                header = "",
                body = jsonBody,
                tags = listOf(),
                metadata = metadata,
                weight = null,
                bandWidthRequest = BandWidthRequest.bandWidthFromComn,
                clientAuthRequest = ClientAuthRequest.empty,
                author = author,
                authorKey = userKeyStore.getKey(UserKeyType.ACTIVE)
            )
        }.resolvedResponse
        val permlink = response!!.message_id.permlink
        return CommentDomain(
            contentId = ContentIdDomain(postIdDomain.communityId, permlink, currentUserRepository.userId),
            author = UserBriefDomain(currentUserRepository.userAvatarUrl, currentUserRepository.userId, currentUserRepository.userName),
            votes = VotesDomain(0, 1, true, false),
            body = MappersFactory().getMapper<PostMapper>(PostMapper::class).map(JSONObject(jsonBody)),
            jsonBody = null,
            childCommentsCount = 0,
            community = CommunityDomain(postIdDomain.communityId, null, "", null, null, 0, 0, false),
            meta = MetaDomain(DatesServerFormatter.formatFromServer(response.metadata),null),
            parent = ParentCommentDomain(null, postIdDomain),
            type = "comment",
            isDeleted = false,
            isMyComment = true,
            commentLevel = 0,
            donations = null
        )

    }

    override suspend fun replyOnComment(parentCommentId: ContentIdDomain, jsonBody: String): CommentDomain {
        val author = currentUserRepository.userId.mapToCyberName()
        val communityId = parentCommentId.communityId
        val response = callProxy.callBC {
            val metadata = DatesServerFormatter.formatToServer(Date())
            commun4j.createComment(
                parentMssgId = MssgidCGalleryStruct(parentCommentId.userId.toCyberName(), parentCommentId.permlink),
                communCode = CyberSymbolCode(communityId.code),
                header = "",
                body = jsonBody,
                tags = listOf(),
                metadata = metadata,
                weight = null,
                bandWidthRequest = BandWidthRequest.bandWidthFromComn,
                clientAuthRequest = ClientAuthRequest.empty,
                author = author,
                authorKey = userKeyStore.getKey(UserKeyType.ACTIVE)
            )
        }.resolvedResponse
        val permlink = response!!.message_id.permlink
        return CommentDomain(contentId = ContentIdDomain(communityId, permlink, currentUserRepository.userId),
            author = UserBriefDomain(currentUserRepository.userAvatarUrl, currentUserRepository.userId, currentUserRepository.userName),
            votes = VotesDomain(0, 0, false, false),
            body = MappersFactory().getMapper<PostMapper>(PostMapper::class).map(JSONObject(jsonBody)),
            jsonBody = null,
            childCommentsCount = 0,
            community = CommunityDomain(communityId, null, "", null, null, 0, 0, false),
            meta = MetaDomain(DatesServerFormatter.formatFromServer(response.metadata),null),
            parent = ParentCommentDomain(parentCommentId, null),
            type = "comment",
            isDeleted = false,
            isMyComment = true,
            commentLevel = 1,
            donations = null)
    }

    override fun recordPostView(postId: ContentIdDomain, deviceId: String) {
        Timber.tag("NET_SOCKET").d("DiscussionRepositoryImpl::recordPostView(postId: $postId, deviceId: $deviceId)")
        callProxy.callSilent {
            commun4j.recordPostView(postId.userId.toCyberName(), postId.communityId.code, postId.permlink, deviceId)
        }
    }
}