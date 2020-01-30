package io.golos.data.repositories.discussion

import com.squareup.moshi.Moshi
import io.golos.commun4j.Commun4j
import io.golos.commun4j.abi.implementation.c.gallery.MssgidCGalleryStruct
import io.golos.commun4j.model.BandWidthRequest
import io.golos.commun4j.model.ClientAuthRequest
import io.golos.commun4j.model.FeedTimeFrame
import io.golos.commun4j.model.FeedType
import io.golos.commun4j.services.model.CommentsSortBy
import io.golos.commun4j.sharedmodel.CyberName
import io.golos.commun4j.sharedmodel.CyberSymbolCode
import io.golos.data.api.discussions.DiscussionsApi
import io.golos.data.api.transactions.TransactionsApi
import io.golos.data.mappers.*
import io.golos.data.network_state.NetworkStateChecker
import io.golos.data.toCyberName
import io.golos.domain.DispatchersProvider
import io.golos.domain.UserKeyStore
import io.golos.domain.commun_entities.Permlink
import io.golos.domain.dto.*
import io.golos.domain.dto.block.ListContentBlockEntity
import io.golos.domain.mappers.CyberPostToEntityMapper
import io.golos.domain.mappers.PostEntitiesToModelMapper
import io.golos.domain.posts_parsing_rendering.mappers.json_to_dto.JsonToDtoMapper
import io.golos.domain.repositories.CurrentUserRepositoryRead
import io.golos.domain.repositories.DiscussionRepository
import io.golos.domain.requestmodel.DeleteDiscussionRequestEntity
import io.golos.domain.requestmodel.DiscussionCreationRequestEntity
import io.golos.domain.use_cases.model.PostModel
import io.golos.domain.use_cases.post.post_dto.ContentBlock
import io.golos.utils.fromServerFormat
import io.golos.utils.toServerFormat
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.io.File
import java.util.*
import javax.inject.Inject
import kotlin.random.Random

class DiscussionRepositoryImpl
@Inject
constructor(
    private val dispatchersProvider: DispatchersProvider,
    networkStateChecker: NetworkStateChecker,
    private val discussionsApi: DiscussionsApi,
    private val postToEntityMapper: CyberPostToEntityMapper,
    private val postToModelMapper: PostEntitiesToModelMapper,
    private val currentUserRepository: CurrentUserRepositoryRead,
    private val transactionsApi: TransactionsApi,
    private val commun4j: Commun4j,
    private val userKeyStore: UserKeyStore,
    private val moshi: Moshi
) : DiscussionCreationRepositoryBase(
    dispatchersProvider,
    discussionsApi,
    networkStateChecker,
    transactionsApi
), DiscussionRepository {

    override suspend fun updatePost(contentIdDomain: ContentIdDomain, body: String, tags: List<String>): ContentIdDomain {
        val postDomain = getPost(contentIdDomain.userId.toCyberName(), contentIdDomain.communityId, contentIdDomain.permlink)

        apiCallChain {
            commun4j.updatePostOrComment(
                messageId = MssgidCGalleryStruct(contentIdDomain.userId.toCyberName(), contentIdDomain.permlink),
                communCode = CyberSymbolCode(contentIdDomain.communityId),
                header = "",
                body = body,
                tags = tags,
                metadata = postDomain.meta.creationTime.toServerFormat(),
                bandWidthRequest = BandWidthRequest.bandWidthFromComn,
                clientAuthRequest = ClientAuthRequest.empty,
                author = currentUserRepository.userId.userId.toCyberName(),
                authorKey = userKeyStore.getKey(UserKeyType.ACTIVE)
            )
        }
        return contentIdDomain
    }

    override suspend fun createPost(communityId: String, body: String, tags: List<String>): ContentIdDomain {
        val createPostResult = apiCallChain {
            commun4j.createPost(
                communCode = CyberSymbolCode(communityId),
                header = "",
                body = body,
                tags = listOf(),
                metadata = Date().toServerFormat(),
                weight = null,
                bandWidthRequest = BandWidthRequest.bandWidthFromComn,
                clientAuthRequest = ClientAuthRequest.empty,
                author = currentUserRepository.userId.userId.toCyberName(),
                authorKey = userKeyStore.getKey(UserKeyType.ACTIVE)
            )
        }

        apiCall {
            commun4j.waitForTransaction(createPostResult.transaction_id)
        }

        return  with(createPostResult.resolvedResponse!!) {
            ContentIdDomain(
                communityId,
                getMessageId.getPermlink,
                getMessageId.getAuthor.name
            )
        }
    }

    override suspend fun uploadContentAttachment(file: File): String {
        return apiCallChain { commun4j.uploadImage(file) }
    }

    override suspend fun getPosts(
        postsConfigurationDomain: PostsConfigurationDomain,
        typeObject: TypeObjectDomain
    ): List<PostDomain> {
        val type = getFeedType(postsConfigurationDomain.typeFeed, typeObject)
        val timeFrame = getFeedTimeFrame(postsConfigurationDomain.timeFrame, postsConfigurationDomain.typeFeed)

        val posts = apiCall {
            commun4j.getPostsRaw(
                if (typeObject != TypeObjectDomain.COMMUNITY) postsConfigurationDomain.userId.toCyberName() else null,
                postsConfigurationDomain.communityId,
                postsConfigurationDomain.communityAlias,
                postsConfigurationDomain.allowNsfw,
                type,
                null,
                timeFrame,
                postsConfigurationDomain.limit,
                postsConfigurationDomain.offset
            )
        }

        // it's a mock for a while
        val contentIds = posts.map { it.contentId.mapToContentIdDomain() }
        val rewards = withContext(dispatchersProvider.ioDispatcher) {
            delay(1000)
            contentIds.map {
                RewardPostDomain(
                    topCount = Random.nextInt(2, 5),
                    collectionEnd = Date(),
                    rewardValue = RewardValueDomain(
                        value = Random.nextDouble(1.0, 999.0),
                        communityId = "ANIM"
                    ),
                    isClosed = Random.nextBoolean(),
                    contentId = it
                )
            }
        }

        return withContext(dispatchersProvider.calculationsDispatcher) {
            posts.items.map { post ->
                val userId = post.author.userId.name
                val reward = rewards.firstOrNull { it.contentId.userId == post.contentId.userId.name && it.contentId.permlink == post.contentId.permlink }
                post.mapToPostDomain(userId == currentUserRepository.userId.userId, reward )
            }
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
        }
    }


    private fun getNewFeedTypeForObject(typeObject: TypeObjectDomain): FeedType {
        return when (typeObject) {
            TypeObjectDomain.USER -> FeedType.BY_USER
            TypeObjectDomain.COMMUNITY -> FeedType.COMMUNITY
            TypeObjectDomain.TRENDING -> FeedType.SUBSCRIPTION
            TypeObjectDomain.MY_FEED -> FeedType.SUBSCRIPTION
        }
    }

    private fun getHotForObject(typeObject: TypeObjectDomain): FeedType {
        return when (typeObject) {
            TypeObjectDomain.USER -> FeedType.BY_USER
            TypeObjectDomain.COMMUNITY -> FeedType.HOT
            TypeObjectDomain.TRENDING -> FeedType.SUBSCRIPTION_HOT
            TypeObjectDomain.MY_FEED -> FeedType.SUBSCRIPTION_HOT
        }
    }

    private fun getPopularForObject(typeObject: TypeObjectDomain): FeedType {
        return when (typeObject) {
            TypeObjectDomain.USER -> FeedType.BY_USER
            TypeObjectDomain.COMMUNITY -> FeedType.TOP_LIKES
            TypeObjectDomain.TRENDING -> FeedType.SUBSCRIPTION_POPULAR
            TypeObjectDomain.MY_FEED -> FeedType.SUBSCRIPTION_POPULAR
        }
    }

    override suspend fun getComments(
        offset: Int,
        pageSize: Int,
        commentType: CommentDomain.CommentTypeDomain,
        userId: UserIdDomain,
        postPermlink: String?,
        communityId: String?,
        communityAlias: String?,
        parentComment: ParentCommentIdentifierDomain?
    ): List<CommentDomain> {
        val currentUserId = currentUserRepository.userId.userId
        return apiCall {
            commun4j.getCommentsRaw(
                sortBy = CommentsSortBy.TIME,
                offset = offset,
                limit = pageSize,
                type = commentType.mapToCommentSortType(),
                userId = userId.mapToCyberName(),
                permlink = postPermlink,
                communityId = communityId,
                communityAlias = communityAlias,
                parentComment = parentComment?.mapToParentComment()
            )
        }.items
            .map { it.mapToCommentDomain(it.author.userId.name == currentUserId) }
    }

    override suspend fun deletePost(permlink: String, communityId: String) {
        apiCallChain {
            commun4j.deletePostOrComment(
                messageId = MssgidCGalleryStruct(currentUserRepository.userId.mapToCyberName(), permlink),
                communCode = CyberSymbolCode(communityId),
                bandWidthRequest = BandWidthRequest.bandWidthFromComn,
                clientAuthRequest = ClientAuthRequest.empty,
                author = currentUserRepository.userId.mapToCyberName(),
                authorKey = userKeyStore.getKey(UserKeyType.ACTIVE)
            )
        }
    }

    override suspend fun deleteComment(permlink: String, communityId: String) {
        apiCallChain {
            commun4j.deletePostOrComment(
                messageId = MssgidCGalleryStruct(currentUserRepository.userId.mapToCyberName(), permlink),
                communCode = CyberSymbolCode(communityId),
                bandWidthRequest = BandWidthRequest.bandWidthFromComn,
                clientAuthRequest = ClientAuthRequest.empty,
                author = currentUserRepository.userId.mapToCyberName(),
                authorKey = userKeyStore.getKey(UserKeyType.ACTIVE)
            )
        }
    }

    override suspend fun updateComment(commentDomain: CommentDomain) {
        val body = commentDomain.body
        val contentEntity = body?.mapToContentBlock()
        val adapter = moshi.adapter(ListContentBlockEntity::class.java)
        val jsonBody = adapter.toJson(contentEntity)
        val contentId = commentDomain.contentId
        apiCallChain {
            commun4j.updatePostOrComment(
                messageId = MssgidCGalleryStruct(contentId.userId.toCyberName(), contentId.permlink),
                communCode = CyberSymbolCode(contentId.communityId),
                header = "",
                body = jsonBody,
                tags = listOf(),
                metadata = commentDomain.meta.creationTime.toServerFormat(),
                bandWidthRequest = BandWidthRequest.bandWidthFromComn,
                clientAuthRequest = ClientAuthRequest.empty,
                author = commentDomain.author.userId.toCyberName(),
                authorKey = userKeyStore.getKey(UserKeyType.ACTIVE))
        }
    }

    override suspend fun reportPost(communityId: String, authorId: String, permlink: String, reason: String) {
        apiCallChain {
            commun4j.reportContent(
                CyberSymbolCode(communityId),
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
        apiCallChain {
            commun4j.upVote(
                communCode = CyberSymbolCode(contentIdDomain.communityId),
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
        apiCallChain {
            commun4j.downVote(
                communCode = CyberSymbolCode(contentIdDomain.communityId),
                messageId = MssgidCGalleryStruct(contentIdDomain.userId.toCyberName(), contentIdDomain.permlink),
                weight = 0,
                bandWidthRequest = BandWidthRequest.bandWidthFromComn,
                clientAuthRequest = ClientAuthRequest.empty,
                voter = currentUser,
                key = userKeyStore.getKey(UserKeyType.ACTIVE)
            )
        }
    }

    private val jsonToDtoMapper: JsonToDtoMapper by lazy { JsonToDtoMapper() }

    override fun createOrUpdate(params: DiscussionCreationRequestEntity): DiscussionCreationResultEntity =
        createOrUpdateDiscussion(params)

    override suspend fun getPost(user: CyberName, communityId: String, permlink: String): PostDomain {
        val post = apiCall { commun4j.getPostRaw(user, communityId, permlink) }

        val reward = withContext(dispatchersProvider.ioDispatcher) {
            delay(1000)
            RewardPostDomain(
                topCount = Random.nextInt(2, 5),
                collectionEnd = Date(),
                rewardValue = RewardValueDomain(
                    value = Random.nextDouble(1.0, 999.0),
                    communityId = "ANIM"
                ),
                isClosed = Random.nextBoolean(),
                contentId = post.contentId.mapToContentIdDomain()
            )
        }

        return post.mapToPostDomain(user.name, reward)
    }

    override fun getPost(user: CyberName, permlink: Permlink): PostModel {
        return discussionsApi.getPost(user, permlink)
            .let { rawPost -> postToEntityMapper.map(rawPost) }
            .let { postEntity -> postToModelMapper.map(postEntity) }
    }

    @Deprecated("")
    override fun deletePost(postId: ContentIdDomain) {
        val request = DeleteDiscussionRequestEntity(Permlink(postId.permlink))
        createOrUpdate(request)
    }

    override suspend fun sendComment(postIdDomain: ContentIdDomain, content: ContentBlock): CommentDomain {
        val contentEntity = content.mapToContentBlock()
        val adapter = moshi.adapter(ListContentBlockEntity::class.java)
        val jsonBody = adapter.toJson(contentEntity)
        val author = currentUserRepository.userId.mapToCyberName()
        val response = apiCallChain {
            val metadata = Date().toServerFormat()
            commun4j.createComment(
                parentMssgId = MssgidCGalleryStruct(postIdDomain.userId.toCyberName(), postIdDomain.permlink),
                communCode = CyberSymbolCode(postIdDomain.communityId),
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
        return CommentDomain(contentId = ContentIdDomain(postIdDomain.communityId, permlink, currentUserRepository.userId.userId),
            author = AuthorDomain(currentUserRepository.userAvatarUrl, currentUserRepository.userId.userId, currentUserRepository.userName),
            votes = VotesDomain(0, 0, false, false),
            body = content,
            childCommentsCount = 0,
            community = CommunityDomain(postIdDomain.communityId, null, "", null, null, 0, 0, false),
            meta = MetaDomain(response!!.metadata.fromServerFormat()),
            parent = ParentCommentDomain(null, postIdDomain),
            type = "comment",
            isDeleted = false,
            isMyComment = true,
            commentLevel = 0)

    }


    override suspend fun replyOnComment(parentCommentId: ContentIdDomain, content: ContentBlock): CommentDomain {
        val contentEntity = content.mapToContentBlock()
        val adapter = moshi.adapter(ListContentBlockEntity::class.java)
        val jsonBody = adapter.toJson(contentEntity)
        val author = currentUserRepository.userId.mapToCyberName()
        val communityId = parentCommentId.communityId
        val response = apiCallChain {
            val metadata = Date().toServerFormat()
            commun4j.createComment(
                parentMssgId = MssgidCGalleryStruct(parentCommentId.userId.toCyberName(), parentCommentId.permlink),
                communCode = CyberSymbolCode(communityId),
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
        return CommentDomain(contentId = ContentIdDomain(communityId, permlink, currentUserRepository.userId.userId),
            author = AuthorDomain(currentUserRepository.userAvatarUrl, currentUserRepository.userId.userId, currentUserRepository.userName),
            votes = VotesDomain(0, 0, false, false),
            body = content,
            childCommentsCount = 0,
            community = CommunityDomain(communityId, null, "", null, null, 0, 0, false),
            meta = MetaDomain(response!!.metadata.fromServerFormat()),
            parent = ParentCommentDomain(parentCommentId, null),
            type = "comment",
            isDeleted = false,
            isMyComment = true,
            commentLevel = 1)
    }
}

