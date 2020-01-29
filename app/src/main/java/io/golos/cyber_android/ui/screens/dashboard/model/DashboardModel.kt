package io.golos.cyber_android.ui.screens.dashboard.model

import io.golos.cyber_android.ui.shared.mvvm.model.ModelBase
import kotlinx.coroutines.flow.Flow

interface DashboardModel: ModelBase{

    suspend fun getNewNotificationsCounterFlow(): Flow<Int>

    suspend fun updateNewNotificationsCounter()
}