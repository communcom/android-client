package io.golos.cyber_android.ui.screens.main_activity.communities.tabs.discover.model

import io.golos.cyber_android.ui.common.mvvm.model.ModelBase
import io.golos.cyber_android.ui.common.recycler_view.ListItem
import io.golos.sharedmodel.Either

interface DiscoverModel: ModelBase {
    suspend fun getFirstPage(): Either<List<ListItem>, Throwable>
}