package io.golos.cyber_android.ui.screens.profile_comments.model

import io.golos.cyber_android.ui.common.mvvm.model.ModelBaseImpl
import io.golos.domain.dto.CommentDomain
import io.golos.domain.dto.ContentIdDomain
import io.golos.domain.dto.UserIdDomain
import io.golos.domain.repositories.DiscussionRepository
import java.io.File
import javax.inject.Inject

class ProfileCommentsModelImpl @Inject constructor(
    private val discussionRepository: DiscussionRepository,
    private val profileUserId: UserIdDomain
) : ProfileCommentsModel, ModelBaseImpl() {

    override suspend fun uploadAttachmentContent(file: File): String = discussionRepository.uploadContentAttachment(file)

    override suspend fun getComments(offset: Int, pageSize: Int): List<CommentDomain> {
        return discussionRepository.getComments(
            offset,
            pageSize,
            CommentDomain.CommentTypeDomain.USER,
            profileUserId
        )
    }

    override suspend fun commentUpVote(commentId: ContentIdDomain) {
        discussionRepository.upVote(
            commentId.communityId,
            commentId.permlink
        )
    }

    override suspend fun commentDownVote(commentId: ContentIdDomain) {
        discussionRepository.downVote(
            commentId.communityId,
            commentId.permlink
        )
    }

    override suspend fun deleteComment(permlink: String, communityId: String) {
        discussionRepository.deletePostOrComment(
            permlink,
            communityId
        )
    }

    override suspend fun updateComment(comment: CommentDomain) {
        discussionRepository.updateComment(comment)
    }
}