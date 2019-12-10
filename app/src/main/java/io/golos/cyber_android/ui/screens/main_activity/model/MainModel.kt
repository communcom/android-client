package io.golos.cyber_android.ui.screens.main_activity.model

import io.golos.cyber_android.ui.common.mvvm.model.ModelBase

interface MainModel : ModelBase {

    suspend fun isNeedShowFtueBoard(): Boolean
}