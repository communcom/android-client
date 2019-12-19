package io.golos.cyber_android.ui.screens.profile_comments.model

import io.golos.cyber_android.ui.common.mvvm.model.ModelBaseImpl
import io.golos.domain.dto.CommentDomain
import io.golos.domain.dto.ContentIdDomain
import io.golos.domain.repositories.DiscussionRepository
import javax.inject.Inject

class ProfileCommentsModelImpl @Inject constructor(
    private val discussionRepository: DiscussionRepository
    ) : ProfileCommentsModel, ModelBaseImpl() {

    override suspend fun getComments(offset: Int, pageSize: Int): List<CommentDomain> {
        return discussionRepository.getComments(
            offset,
            pageSize,
            CommentDomain.CommentTypeDomain.USER
        )
    }

    override suspend fun commentUpVote(commentId: ContentIdDomain) {
        discussionRepository.upVote(
            commentId.communityId,
            commentId.userId,
            commentId.permlink
        )
    }

    override suspend fun commentDownVote(commentId: ContentIdDomain) {
        discussionRepository.downVote(
            commentId.communityId,
            commentId.userId,
            commentId.permlink
        )
    }

    override suspend fun deleteComment(userId: String, permlink: String, communityId: String) {
        discussionRepository.deletePostOrComment(
            userId,
            permlink,
            communityId
        )
    }

    override suspend fun updateComment(comment: CommentDomain) {
        discussionRepository.updateComment(comment)
    }
}