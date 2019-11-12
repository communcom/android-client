package io.golos.cyber_android.ui.screens.main_activity.communities.model

import io.golos.cyber_android.ui.common.mvvm.model.ModelBase
import io.golos.cyber_android.ui.screens.main_activity.communities.dto.PageLoadResult

interface CommunitiesModel: ModelBase {
    fun initModel(controlHeight: Int)

    fun canLoad(lastVisibleItemPosition: Int): Boolean

    suspend fun getPage(lastVisibleItemPosition: Int): PageLoadResult
}