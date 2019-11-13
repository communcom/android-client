package io.golos.data.repositories.discussion

import io.golos.commun4j.Commun4j
import io.golos.commun4j.model.FeedSortByType
import io.golos.commun4j.model.FeedTimeFrame
import io.golos.commun4j.model.FeedType
import io.golos.commun4j.sharedmodel.CyberName
import io.golos.data.api.discussions.DiscussionsApi
import io.golos.data.api.transactions.TransactionsApi
import io.golos.data.mappers.CyberDiscussionRawToPostDomainMapper
import io.golos.domain.repositories.CurrentUserRepositoryRead
import io.golos.data.toCyberName
import io.golos.data.utils.getOrThrow
import io.golos.domain.commun_entities.Permlink
import io.golos.domain.dto.CyberUser
import io.golos.domain.dto.DiscussionCreationResultEntity
import io.golos.domain.dto.PostDomain
import io.golos.domain.dto.PostsConfigurationDomain
import io.golos.domain.extensions.asElapsedTime
import io.golos.domain.use_cases.model.*
import io.golos.domain.mappers.CyberPostToEntityMapper
import io.golos.domain.mappers.PostEntitiesToModelMapper
import io.golos.domain.posts_parsing_rendering.mappers.comment_to_json.CommentToJsonMapper
import io.golos.domain.posts_parsing_rendering.mappers.json_to_dto.JsonToDtoMapper
import io.golos.domain.repositories.DiscussionRepository
import io.golos.domain.requestmodel.DeleteDiscussionRequestEntity
import io.golos.domain.requestmodel.DiscussionCreationRequestEntity
import java.util.*
import javax.inject.Inject

class DiscussionRepositoryImpl
@Inject
constructor(
    private val discussionsApi: DiscussionsApi,
    private val postToEntityMapper: CyberPostToEntityMapper,
    private val postToModelMapper: PostEntitiesToModelMapper,
    private val currentUserRepository: CurrentUserRepositoryRead,
    private val transactionsApi: TransactionsApi,
    private val commun4j: Commun4j
): DiscussionCreationRepositoryBase(
    discussionsApi,
    transactionsApi
), DiscussionRepository {

    override suspend fun getPosts(postsConfigurationDomain: PostsConfigurationDomain): List<PostDomain> {
        return commun4j.getPostsRaw(
            postsConfigurationDomain.userId.toCyberName(),
            postsConfigurationDomain.communityId,
            postsConfigurationDomain.communityAlias,
            postsConfigurationDomain.allowNsfw,
            FeedType.TOP_COMENTS,
            FeedSortByType.TIME,
            FeedTimeFrame.ALL,
            postsConfigurationDomain.limit,
            postsConfigurationDomain.offset
        ).getOrThrow()
            .items
            .map {
                CyberDiscussionRawToPostDomainMapper().invoke(it)
            }
    }

    private val jsonToDtoMapper: JsonToDtoMapper by lazy { JsonToDtoMapper() }

    override fun createOrUpdate(params: DiscussionCreationRequestEntity): DiscussionCreationResultEntity =
        createOrUpdateDiscussion(params)

    override fun getPost(user: CyberName, permlink: Permlink): PostModel =
        discussionsApi.getPost(user, permlink)
            .let { rawPost -> postToEntityMapper.map(rawPost) }
            .let { postEntity -> postToModelMapper.map(postEntity) }

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

        val newComment = comment.copy(content = CommentContentModel(
            body = ContentBodyModel(jsonToDtoMapper.map(contentAsJson)),
            commentLevel = comment.content.commentLevel
        ))

        return newComment
    }

    override fun createReplyComment(repliedCommentId: DiscussionIdModel, newCommentText: String): CommentModel =
        createComment(repliedCommentId, newCommentText, 1)

    private fun createCommentModel(
        contentAsJson: String,
        parentId: DiscussionIdModel,
        author: DiscussionAuthorModel,
        permlink: Permlink,
        commentLevel: Int) =
        CommentModel(
            contentId = DiscussionIdModel(currentUserRepository.userId, permlink),
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
            CyberUser(currentUserRepository.userId),
            currentUserRepository.authState!!.userName,
            currentUserRepository.userAvatarUrl)
        val permlink = Permlink.generate()

        val apiAnswer = discussionsApi.createComment(contentAsJson, parentId, author, permlink)
        transactionsApi.waitForTransaction(apiAnswer.first.transaction_id)

        return createCommentModel(contentAsJson, parentId, author, permlink, commentLevel)
    }
}