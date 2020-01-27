package io.golos.cyber_android.ui.screens.notifications.model

import io.golos.cyber_android.ui.shared.mvvm.model.ModelBaseImpl
import io.golos.domain.dto.BaseNotificationDomain
import io.golos.domain.repositories.NotificationsRepository
import javax.inject.Inject


class NotificationsModelImpl @Inject constructor(private val repository: NotificationsRepository) : NotificationsModel,
    ModelBaseImpl() {

    override suspend fun getNotifications(limit: Int): List<BaseNotificationDomain> {
        return repository.getNotifications(limit)
    }
}