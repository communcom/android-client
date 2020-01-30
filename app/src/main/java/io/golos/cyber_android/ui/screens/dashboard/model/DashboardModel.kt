package io.golos.cyber_android.ui.screens.dashboard.model

import io.golos.cyber_android.ui.shared.mvvm.model.ModelBase
import io.golos.domain.dto.NotificationsStatusDomain
import kotlinx.coroutines.flow.Flow

interface DashboardModel: ModelBase{

    suspend fun getNewNotificationsCounterFlow(): Flow<NotificationsStatusDomain>

    suspend fun updateNewNotificationsCounter()
}