package io.golos.cyber_android.ui.screens.main_activity.communities.tabs.common.model

import io.golos.commun4j.sharedmodel.Either
import io.golos.cyber_android.ui.common.mvvm.model.ModelBase
import io.golos.cyber_android.ui.common.recycler_view.ListItem
import io.golos.cyber_android.ui.screens.main_activity.communities.tabs.common.dto.PageLoadResult

interface CommunityModel: ModelBase {
    fun initModel(controlHeight: Int)

    fun canLoad(lastVisibleItemPosition: Int): Boolean

    suspend fun getPage(lastVisibleItemPosition: Int): PageLoadResult

    fun search(searchTest: String)

    fun setOnSearchResultListener(listener: (Either<List<ListItem>?, Throwable>) -> Unit)

    fun close()
}