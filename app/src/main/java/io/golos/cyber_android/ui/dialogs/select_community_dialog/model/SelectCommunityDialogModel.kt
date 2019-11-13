package io.golos.cyber_android.ui.dialogs.select_community_dialog.model

import io.golos.commun4j.sharedmodel.Either
import io.golos.cyber_android.ui.common.mvvm.model.ModelBase
import io.golos.cyber_android.ui.common.recycler_view.versioned.VersionedListItem
import io.golos.cyber_android.ui.screens.main_activity.communities.model.CommunitiesModel

interface SelectCommunityDialogModel : CommunitiesModel {
    fun search(searchText: String)

    fun setOnSearchResultListener(listener: (Either<List<VersionedListItem>?, Throwable>) -> Unit)

    fun close()
}