package io.golos.cyber_android.ui.screens.post_view.model.post_list_data_source

interface PostListDataSourcePostControls {
    suspend fun updatePostVoteStatus(isUpVoteActive: Boolean?, isDownVoteActive: Boolean?, voteBalanceDelta: Long)
}