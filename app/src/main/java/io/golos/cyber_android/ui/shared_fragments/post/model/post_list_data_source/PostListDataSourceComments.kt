package io.golos.cyber_android.ui.shared_fragments.post.model.post_list_data_source

import io.golos.domain.interactors.model.CommentModel

interface PostListDataSourceComments {
    suspend fun addLoadingCommentsIndicator(isFirstLevel: Boolean)

    suspend fun addRetryLoadingComments(isFirstLevel: Boolean)

    suspend fun addFirstLevelComments(comments: List<CommentModel>)
}