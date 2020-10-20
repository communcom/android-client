package io.golos.cyber_android.ui.dialogs.select_community_dialog.model

import io.golos.commun4j.sharedmodel.Either
import io.golos.cyber_android.ui.screens.communities_list.model.CommunitiesListModel
import io.golos.cyber_android.ui.shared.recycler_view.versioned.VersionedListItem

interface SelectCommunityDialogModel : CommunitiesListModel {
    fun search(searchText: String)

    fun setOnSearchResultListener(listener: (Either<List<VersionedListItem>?, Throwable>) -> Unit)

    fun close()
}