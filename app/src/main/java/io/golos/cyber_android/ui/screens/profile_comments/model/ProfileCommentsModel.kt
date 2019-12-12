package io.golos.cyber_android.ui.screens.profile_comments.model

import io.golos.cyber_android.ui.common.mvvm.model.ModelBase
import io.golos.domain.dto.CommentDomain

interface ProfileCommentsModel : ModelBase {

    suspend fun getComments(): List<CommentDomain>

    suspend fun upVote(commentId: String)

    suspend fun downVote(commentId: String)

}