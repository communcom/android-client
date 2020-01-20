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
import io.golos.domain.extensions.asElapsedTime
import io.golos.domain.mappers.CyberPostToEntityMapper
import io.golos.domain.mappers.PostEntitiesToModelMapper
import io.golos.domain.posts_parsing_rendering.PostGlobalConstants
import io.golos.domain.posts_parsing_rendering.PostTypeJson.COMMENT
import io.golos.domain.posts_parsing_rendering.mappers.comment_to_json.CommentToJsonMapper
import io.golos.domain.posts_parsing_rendering.mappers.json_to_dto.JsonToDtoMapper
import io.golos.domain.repositories.CurrentUserRepositoryRead
import io.golos.domain.repositories.DiscussionRepository
import io.golos.domain.requestmodel.DeleteDiscussionRequestEntity
import io.golos.domain.requestmodel.DiscussionCreationRequestEntity
import io.golos.domain.use_cases.model.*
import io.golos.domain.use_cases.post.post_dto.*
import io.golos.utils.toServerFormat
import java.io.File
import java.util.*
import javax.inject.Inject

class DiscussionRepositoryImpl
@Inject
constructor(
    dispatchersProvider: DispatchersProvider,
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
        return apiCall {
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
        }.items.map {
            val userId = it.author.userId.name
            it.mapToPostDomain(userId == currentUserRepository.userId.userId)
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
        permlink: String?,
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
                permlink = permlink,
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
                author = currentUserRepository.userId.mapToCyberName()
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
                author = currentUserRepository.userId.mapToCyberName()
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
                author = commentDomain.author.userId.toCyberName()
            )
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
        return apiCall {
            commun4j.getPostRaw(user, communityId, permlink)
        }.mapToPostDomain(user.name)
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

    override suspend fun createCommentForPost(
        postId: DiscussionIdModel,
        contentId: ContentIdDomain,
        commentText: String
    ): CommentModel =
        createComment(postId, contentId, commentText, 0)

    override fun deleteComment(commentId: DiscussionIdModel) {
        val apiAnswer = discussionsApi.deleteComment(commentId.permlink)
        transactionsApi.waitForTransaction(apiAnswer.first.transaction_id)
    }

    override fun updateCommentText(comment: CommentModel, newCommentText: String): CommentModel {
        val contentAsJson = CommentToJsonMapper.mapTextToJson(newCommentText)

        val newComment = comment.copy(
            body = jsonToDtoMapper.map(contentAsJson),
            content = CommentContentModel(ContentBodyModel(jsonToDtoMapper.map(contentAsJson)), comment.commentLevel),
            commentLevel = comment.commentLevel
        )

        return newComment
    }

    override suspend fun createReplyComment(
        repliedCommentId: DiscussionIdModel,
        contentId: ContentIdDomain,
        newCommentText: String
    ): CommentModel =
        createComment(repliedCommentId, contentId, newCommentText, 1)

    private fun createCommentModel(
        contentAsJson: String,
        parentId: DiscussionIdModel,
        author: DiscussionAuthorModel,
        permlink: Permlink,
        commentLevel: Int
    ) =
        CommentModel(
            contentId = DiscussionIdModel(currentUserRepository.userId.userId, permlink),
            author = author,
            body = jsonToDtoMapper.map(contentAsJson),
            content = CommentContentModel(ContentBodyModel(jsonToDtoMapper.map(contentAsJson)), commentLevel),
            commentLevel = commentLevel,
            votes = DiscussionVotesModel(hasUpVote = false, hasDownVote = false, upCount = 0, downCount = 0),
            payout = DiscussionPayoutModel(),
            parentId = parentId,
            meta = DiscussionMetadataModel(Date(), Date().asElapsedTime()),
            stats = DiscussionStatsModel(0.toBigInteger(), 0),
            childTotal = 0,
            child = listOf()
        )

    private suspend fun createComment(
        parentId: DiscussionIdModel,
        contentIdDomain: ContentIdDomain,
        commentText: String,
        commentLevel: Int
    ): CommentModel {
        val contentAsJson = CommentToJsonMapper.mapTextToJson(commentText)
        val author = DiscussionAuthorModel(
            CyberUser(currentUserRepository.userId.userId),
            currentUserRepository.authState!!.userName,
            currentUserRepository.userAvatarUrl
        )
        val permlink = Permlink.generate()
        val authorCyberName = currentUserRepository.userId.userId.toCyberName()
        apiCallChain {
            commun4j.createComment(
                parentMssgId = MssgidCGalleryStruct(authorCyberName, parentId.permlink.value),
                communCode = CyberSymbolCode(contentIdDomain.communityId),
                header = "",
                body = contentAsJson,
                tags = listOf(),
                metadata = Date().toServerFormat(),
                weight = null,
                bandWidthRequest = BandWidthRequest.bandWidthFromComn,
                clientAuthRequest = ClientAuthRequest.empty,
                author = authorCyberName,
                authorKey = userKeyStore.getKey(UserKeyType.ACTIVE)
            )
        }

        /* val apiAnswer = discussionsApi.createComment(contentAsJson, parentId, author, permlink)
         transactionsApi.waitForTransaction(apiAnswer.first.transaction_id)*/

        return createCommentModel(contentAsJson, parentId, author, permlink, commentLevel)
    }

    override suspend fun createComment(postIdDomain: ContentIdDomain, content: List<Block>, attachments: AttachmentsBlock?) {
        val contentBlock = ContentBlock(
            id = PostGlobalConstants.postFormatVersion.toString(),
            type = COMMENT,
            metadata = PostMetadata(PostGlobalConstants.postFormatVersion, PostType.COMMENT),
            title = "",
            content = content,
            attachments = attachments
        )
        val contentEntity = contentBlock.mapToContentBlock()
        val adapter = moshi.adapter(ListContentBlockEntity::class.java)
        val jsonBody = adapter.toJson(contentEntity)
        val author = currentUserRepository.userId.mapToCyberName()
        apiCallChain {
            commun4j.createComment(
                parentMssgId = MssgidCGalleryStruct(postIdDomain.userId.toCyberName(), postIdDomain.permlink),
                communCode = CyberSymbolCode(postIdDomain.communityId),
                header = "",
                body = jsonBody,
                tags = listOf(),
                metadata = Date().toServerFormat(),
                weight = null,
                bandWidthRequest = BandWidthRequest.bandWidthFromComn,
                clientAuthRequest = ClientAuthRequest.empty,
                author = author,
                authorKey = userKeyStore.getKey(UserKeyType.ACTIVE)
            )
        }
    }
}

