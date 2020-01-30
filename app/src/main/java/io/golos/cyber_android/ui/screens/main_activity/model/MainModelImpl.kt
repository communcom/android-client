package io.golos.cyber_android.ui.screens.main_activity.model

import io.golos.cyber_android.ui.shared.mvvm.model.ModelBaseImpl
import io.golos.domain.repositories.NotificationsRepository
import io.golos.domain.repositories.UsersRepository
import javax.inject.Inject

class MainModelImpl @Inject constructor(private val usersRepository: UsersRepository, private val notificationsRepository: NotificationsRepository): MainModel, ModelBaseImpl() {

    override suspend fun unsubscribeOnNotificationsChanges() {
        notificationsRepository.unsubscribeOnNotificationsChanges()
    }

    override suspend fun subscribeOnNotificationsChanges() {
        notificationsRepository.subscribeOnNotificationsChanges()
    }

    override suspend fun isNeedShowFtueBoard(): Boolean = usersRepository.isNeedShowFtueBoard()
}