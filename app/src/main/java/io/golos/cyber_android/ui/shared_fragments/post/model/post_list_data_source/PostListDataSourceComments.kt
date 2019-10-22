package io.golos.cyber_android.ui.shared_fragments.post.model.post_list_data_source

import io.golos.domain.interactors.model.CommentModel

interface PostListDataSourceComments {
    suspend fun addFirstLevelComments(comments: List<CommentModel>)
}