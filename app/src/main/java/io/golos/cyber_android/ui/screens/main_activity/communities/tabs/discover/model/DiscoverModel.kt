package io.golos.cyber_android.ui.screens.main_activity.communities.tabs.discover.model

import io.golos.cyber_android.ui.common.mvvm.model.ModelBase
import io.golos.cyber_android.ui.screens.main_activity.communities.tabs.discover.dto.PageLoadResult
import io.golos.sharedmodel.Either

interface DiscoverModel: ModelBase {
    fun initModel(controlHeight: Int)

    fun canLoad(lastVisibleItemPosition: Int): Boolean

    suspend fun getPage(lastVisibleItemPosition: Int): Either<PageLoadResult, PageLoadResult>
}