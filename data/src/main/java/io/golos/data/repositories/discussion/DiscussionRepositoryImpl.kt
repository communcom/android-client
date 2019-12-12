package io.golos.data.repositories.discussion

import io.golos.commun4j.Commun4j
import io.golos.commun4j.abi.implementation.c.gallery.MssgidCGalleryStruct
import io.golos.commun4j.model.*
import io.golos.commun4j.sharedmodel.CyberName
import io.golos.commun4j.sharedmodel.CyberSymbolCode
import io.golos.commun4j.utils.StringSigner
import io.golos.data.api.discussions.DiscussionsApi
import io.golos.data.api.transactions.TransactionsApi
import io.golos.data.mappers.mapToPostDomain
import io.golos.data.toCyberName
import io.golos.domain.DispatchersProvider
import io.golos.domain.UserKeyStore
import io.golos.domain.commun_entities.Permlink
import io.golos.domain.dto.*
import io.golos.domain.extensions.asElapsedTime
import io.golos.domain.mappers.CyberPostToEntityMapper
import io.golos.domain.mappers.PostEntitiesToModelMapper
import io.golos.domain.posts_parsing_rendering.mappers.comment_to_json.CommentToJsonMapper
import io.golos.domain.posts_parsing_rendering.mappers.json_to_dto.JsonToDtoMapper
import io.golos.domain.repositories.CurrentUserRepositoryRead
import io.golos.domain.repositories.DiscussionRepository
import io.golos.domain.requestmodel.DeleteDiscussionRequestEntity
import io.golos.domain.requestmodel.DiscussionCreationRequestEntity
import io.golos.domain.use_cases.model.*
import org.spongycastle.crypto.tls.ConnectionEnd.client
import java.util.*
import javax.inject.Inject

class DiscussionRepositoryImpl
@Inject
constructor(
    dispatchersProvider: DispatchersProvider,
    private val discussionsApi: DiscussionsApi,
    private val postToEntityMapper: CyberPostToEntityMapper,
    private val postToModelMapper: PostEntitiesToModelMapper,
    private val currentUserRepository: CurrentUserRepositoryRead,
    private val transactionsApi: TransactionsApi,
    private val commun4j: Commun4j,
    private val userKeyStore: UserKeyStore
) : DiscussionCreationRepositoryBase(
    dispatchersProvider,
    discussionsApi,
    transactionsApi
), DiscussionRepository {

    override suspend fun getPosts(postsConfigurationDomain: PostsConfigurationDomain): List<PostDomain> {
        val type = FeedType.valueOf(postsConfigurationDomain.typeFeed.name)
        val sortByType = FeedSortByType.valueOf(postsConfigurationDomain.sortBy.name)
        val timeFrame = FeedTimeFrame.valueOf(postsConfigurationDomain.timeFrame.name)
        val items = apiCall {
            commun4j.getPostsRaw(
                postsConfigurationDomain.userId.toCyberName(),
                postsConfigurationDomain.communityId,
                postsConfigurationDomain.communityAlias,
                postsConfigurationDomain.allowNsfw,
                type,
                sortByType,
                timeFrame,
                postsConfigurationDomain.limit,
                postsConfigurationDomain.offset
            )
        }.items
        return items.map {
            val userId = it.author.userId.name
            it.mapToPostDomain(userId == currentUserRepository.userId.userId)
        }
    }

    override suspend fun reportPost(communityId: String, authorId: String, permlink: String, reason: String) {

        val reporter = "cmn5bzqfmjtw".toCyberName()
        val userName = "kirlin-lenita-iii"
        val activeKey = "5JAGy2NbZTgDYMb59QKA5YdbSkzvhg9Y4YhriudPk8nvGFr8acs"
        /*val secret = client.getAuthSecret().getOrThrow().secret
        commun4j.authWithSecret(userName, secret, StringSigner.signString(secret, activeKey)).getOrThrow()
        val post = client.getPosts(type = FeedType.NEW, limit = 1).getOrThrow().first()

            .getOrThrow()*/

        /*apiCallChain{
            commun4j.reportContent(CyberSymbolCode( post.community.communityId),
                MssgidCGalleryStruct(post.author.userId, post.contentId.permlink),
                "[\"NSFW\"]",
                BandWidthRequest.bandWidthFromComn,
                ClientAuthRequest.empty,
                reporter,
                activeKey)
        }*/
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

    override suspend fun upVote(
        communityId: String,
        userId: String,
        permlink: String
    ) {
        apiCallChain {
            commun4j.upVote(
                communCode = CyberSymbolCode(communityId),
                messageId = MssgidCGalleryStruct(userId.toCyberName(), permlink),
                weight = 0,
                bandWidthRequest = BandWidthRequest.bandWidthFromComn,
                clientAuthRequest = ClientAuthRequest.empty,
                voter = userId.toCyberName()
            )
        }
    }

    override suspend fun downVote(
        communityId: String,
        userId: String,
        permlink: String
    ) {
        apiCallChain {
            commun4j.downVote(
                communCode = CyberSymbolCode(communityId),
                messageId = MssgidCGalleryStruct(userId.toCyberName(), permlink),
                weight = 0,
                bandWidthRequest = BandWidthRequest.bandWidthFromComn,
                clientAuthRequest = ClientAuthRequest.empty,
                voter = userId.toCyberName()
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

    override fun deletePost(postId: DiscussionIdModel) {
        val request = DeleteDiscussionRequestEntity(postId.permlink)
        createOrUpdate(request)
    }

    override fun createCommentForPost(postId: DiscussionIdModel, commentText: String): CommentModel =
        createComment(postId, commentText, 0)

    override fun deleteComment(commentId: DiscussionIdModel) {
        val apiAnswer = discussionsApi.deleteComment(commentId.permlink)
        transactionsApi.waitForTransaction(apiAnswer.first.transaction_id)
    }

    override fun updateCommentText(comment: CommentModel, newCommentText: String): CommentModel {
        val contentAsJson = CommentToJsonMapper.mapTextToJson(newCommentText)

        val newComment = comment.copy(
            content = CommentContentModel(
                body = ContentBodyModel(jsonToDtoMapper.map(contentAsJson)),
                commentLevel = comment.content.commentLevel
            )
        )

        return newComment
    }

    override fun createReplyComment(repliedCommentId: DiscussionIdModel, newCommentText: String): CommentModel =
        createComment(repliedCommentId, newCommentText, 1)

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
            content = CommentContentModel(
                body = ContentBodyModel(jsonToDtoMapper.map(contentAsJson)),
                commentLevel = commentLevel
            ),
            votes = DiscussionVotesModel(hasUpVote = false, hasDownVote = false, upCount = 0, downCount = 0),
            payout = DiscussionPayoutModel(),
            parentId = parentId,
            meta = DiscussionMetadataModel(Date(), Date().asElapsedTime()),
            stats = DiscussionStatsModel(0.toBigInteger(), 0),
            childTotal = 0,
            child = listOf()
        )

    private fun createComment(parentId: DiscussionIdModel, commentText: String, commentLevel: Int): CommentModel {
        val contentAsJson = CommentToJsonMapper.mapTextToJson(commentText)
        val author = DiscussionAuthorModel(
            CyberUser(currentUserRepository.userId.userId),
            currentUserRepository.authState!!.userName,
            currentUserRepository.userAvatarUrl
        )
        val permlink = Permlink.generate()

        val apiAnswer = discussionsApi.createComment(contentAsJson, parentId, author, permlink)
        transactionsApi.waitForTransaction(apiAnswer.first.transaction_id)

        return createCommentModel(contentAsJson, parentId, author, permlink, commentLevel)
    }
}

