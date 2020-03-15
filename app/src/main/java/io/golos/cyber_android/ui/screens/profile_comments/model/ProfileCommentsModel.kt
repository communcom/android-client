package io.golos.cyber_android.ui.screens.profile_comments.model

import io.golos.cyber_android.ui.shared.mvvm.model.ModelBase
import io.golos.domain.dto.CommentDomain
import io.golos.domain.dto.CommunityIdDomain
import io.golos.domain.dto.ContentIdDomain
import java.io.File

interface ProfileCommentsModel : ModelBase {

    suspend fun getComments(offset: Int, pageSize: Int): List<CommentDomain>

    suspend fun commentUpVote(commentId: ContentIdDomain)

    suspend fun commentDownVote(commentId: ContentIdDomain)

    suspend fun deleteComment(permlink: String, communityId: CommunityIdDomain)

    suspend fun updateComment(comment: CommentDomain)

    suspend fun uploadAttachmentContent(file: File): String
}