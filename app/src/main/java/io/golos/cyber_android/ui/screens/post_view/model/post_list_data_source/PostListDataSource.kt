package io.golos.cyber_android.ui.screens.post_view.model.post_list_data_source

import androidx.lifecycle.LiveData
import io.golos.cyber_android.ui.screens.post_view.dto.SortingType
import io.golos.cyber_android.ui.shared.recycler_view.versioned.VersionedListItem
import io.golos.domain.dto.DonationsDomain
import io.golos.domain.dto.PostDomain

interface PostListDataSource {
    val post: LiveData<List<VersionedListItem>>

    suspend fun createOrUpdatePostData(postDomain: PostDomain)

    suspend fun updateCommentsSorting(sortingType: SortingType)

    suspend fun addCommentsHeader()

    suspend fun updateDonation(donation: DonationsDomain)
}