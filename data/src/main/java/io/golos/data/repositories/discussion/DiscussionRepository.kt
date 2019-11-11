package io.golos.data.repositories.discussion

import io.golos.commun4j.sharedmodel.CyberName
import io.golos.domain.commun_entities.Permlink
import io.golos.domain.dto.DiscussionCreationResultEntity
import io.golos.domain.use_cases.model.CommentModel
import io.golos.domain.use_cases.model.DiscussionIdModel
import io.golos.domain.use_cases.model.PostModel
import io.golos.domain.requestmodel.DiscussionCreationRequestEntity

interface DiscussionRepository {
    fun createOrUpdate(params: DiscussionCreationRequestEntity): DiscussionCreationResultEntity

    fun getPost(user: CyberName, permlink: Permlink): PostModel

    fun deletePost(postId: DiscussionIdModel)

    fun createCommentForPost(postId: DiscussionIdModel, commentText: String): CommentModel

    fun deleteComment(commentId: DiscussionIdModel)

    fun updateCommentText(comment: CommentModel, newCommentText: String): CommentModel

    fun createReplyComment(repliedCommentId: DiscussionIdModel, newCommentText: String): CommentModel
}