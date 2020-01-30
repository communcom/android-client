package io.golos.cyber_android.ui.screens.main_activity.model

import io.golos.cyber_android.ui.shared.mvvm.model.ModelBase

interface MainModel : ModelBase {

    suspend fun isNeedShowFtueBoard(): Boolean

    suspend fun subscribeOnNotificationsChanges()

    suspend fun unsubscribeOnNotificationsChanges()
}