package io.golos.cyber_android.ui.screens.profile_comments.model

import io.golos.cyber_android.ui.shared.mvvm.model.ModelBaseImpl
import io.golos.domain.dto.CommentDomain
import io.golos.domain.dto.CommunityIdDomain
import io.golos.domain.dto.ContentIdDomain
import io.golos.domain.dto.UserIdDomain
import io.golos.domain.repositories.DiscussionRepository
import io.golos.domain.repositories.UsersRepository
import java.io.File
import javax.inject.Inject

class ProfileCommentsModelImpl @Inject constructor(
    private val discussionRepository: DiscussionRepository,
    private val profileUserId: UserIdDomain,
    private val usersRepository: UsersRepository
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
        discussionRepository.upVote(commentId)
    }

    override suspend fun commentDownVote(commentId: ContentIdDomain) {
        discussionRepository.downVote(commentId)
    }

    override suspend fun deleteComment(permlink: String, communityId: CommunityIdDomain) {
        discussionRepository.deletePost(permlink, communityId)
    }

    override suspend fun updateComment(comment: CommentDomain) {
        discussionRepository.updateComment(comment)
    }

    override suspend fun getUserId(userNameOrId: String): UserIdDomain = usersRepository.getUserId(userNameOrId)
}