package io.golos.cyber_android.ui.screens.dashboard.model

import io.golos.domain.dto.NotificationsStatusDomain
import io.golos.domain.repositories.NotificationsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DashboardModelImpl @Inject constructor(private val notificationsRepository: NotificationsRepository): DashboardModel {

    override suspend fun getNewNotificationsCounterFlow(): Flow<NotificationsStatusDomain> = notificationsRepository.getNewNotificationsCounterFlow()

    override suspend fun updateNewNotificationsCounter() = notificationsRepository.updateNewNotificationsCounter()
}