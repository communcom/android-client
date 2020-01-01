package io.golos.cyber_android.ui.screens.main_activity.model

import io.golos.cyber_android.ui.shared.mvvm.model.ModelBaseImpl
import io.golos.domain.repositories.UsersRepository
import javax.inject.Inject

class MainModelImpl @Inject constructor(private val usersRepository: UsersRepository): MainModel, ModelBaseImpl() {

    override suspend fun isNeedShowFtueBoard(): Boolean = usersRepository.isNeedShowFtueBoard()
}