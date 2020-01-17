package io.golos.cyber_android.ui.screens.community_page_friends.model

import androidx.lifecycle.LiveData
import io.golos.cyber_android.ui.shared.mvvm.model.ModelBase
import io.golos.cyber_android.ui.shared.recycler_view.versioned.VersionedListItem

interface CommunityPageFriendsModel : ModelBase {
    val title: String

    fun getItems() : LiveData<List<VersionedListItem>>

    suspend fun loadPage()
}