package io.golos.cyber_android.ui.screens.post_view.model.post_list_data_source

import androidx.lifecycle.LiveData
import io.golos.cyber_android.ui.common.recycler_view.versioned.VersionedListItem
import io.golos.cyber_android.ui.screens.post_view.dto.SortingType
import io.golos.domain.dto.PostDomain

interface PostListDataSource {
    val post: LiveData<List<VersionedListItem>>

    suspend fun createOrUpdatePostData(postDomain: PostDomain)

    suspend fun updateCommentsSorting(sortingType: SortingType)
}