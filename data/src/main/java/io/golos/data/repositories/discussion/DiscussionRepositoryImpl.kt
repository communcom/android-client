package io.golos.data.repositories.discussion

import io.golos.commun4j.sharedmodel.CyberName
import io.golos.data.api.discussions.DiscussionsApi
import io.golos.data.api.transactions.TransactionsApi
import io.golos.data.repositories.current_user_repository.CurrentUserRepositoryRead
import io.golos.domain.commun_entities.Permlink
import io.golos.domain.entities.CyberUser
import io.golos.domain.entities.DiscussionCreationResultEntity
import io.golos.domain.extensions.asElapsedTime
import io.golos.domain.interactors.model.*
import io.golos.domain.mappers.CyberPostToEntityMapper
import io.golos.domain.mappers.PostEntitiesToModelMapper
import io.golos.domain.posts_parsing_rendering.mappers.comment_to_json.CommentToJsonMapper
import io.golos.domain.posts_parsing_rendering.mappers.json_to_dto.JsonToDtoMapper
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
    private val transactionsApi: TransactionsApi
): DiscussionCreationRepositoryBase(
    discussionsApi,
    transactionsApi
), DiscussionRepository {

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

    override fun createCommentForPost(commentText: String, postId: DiscussionIdModel): CommentModel {
        val contentAsJson = CommentToJsonMapper.mapTextToJson(commentText)
        val author = DiscussionAuthorModel(
            CyberUser(currentUserRepository.userId),
            currentUserRepository.authState!!.userName,
            currentUserRepository.userAvatarUrl)
        val permlink = Permlink.generate()

        val apiAnswer = discussionsApi.createComment(contentAsJson, postId, author, permlink)
        transactionsApi.waitForTransaction(apiAnswer.first.transaction_id)

        return createCommentModel(contentAsJson, postId, author, permlink)
    }

    override fun deleteComment(commentId: DiscussionIdModel) {
        val apiAnswer = discussionsApi.deleteComment(commentId.permlink)
        transactionsApi.waitForTransaction(apiAnswer.first.transaction_id)
    }

    private fun createCommentModel(contentAsJson: String, postId: DiscussionIdModel, author: DiscussionAuthorModel, permlink: Permlink) =
        CommentModel(
            contentId = DiscussionIdModel(currentUserRepository.userId, permlink),
            author = author,
            content = CommentContentModel(
                body = ContentBodyModel(jsonToDtoMapper.map(contentAsJson)),
                commentLevel = 0
            ),
            votes = DiscussionVotesModel(hasUpVote = false, hasDownVote = false, upCount = 0, downCount = 0),
            payout = DiscussionPayoutModel(),
            parentId = postId,
            meta = DiscussionMetadataModel(Date(), Date().asElapsedTime()),
            stats = DiscussionStatsModel(0.toBigInteger(), 0),
            childTotal = 0,
            child = listOf()
        )
}