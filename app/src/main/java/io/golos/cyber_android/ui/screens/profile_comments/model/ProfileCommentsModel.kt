package io.golos.cyber_android.ui.screens.profile_comments.model

import io.golos.cyber_android.ui.common.mvvm.model.ModelBase
import io.golos.domain.dto.CommentDomain
import io.golos.domain.dto.ContentIdDomain

interface ProfileCommentsModel : ModelBase {

    suspend fun getComments(offset: Int, pageSize: Int): List<CommentDomain>

    suspend fun commentUpVote(commentId: ContentIdDomain)

    suspend fun commentDownVote(commentId: ContentIdDomain)

}