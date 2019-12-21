package io.golos.cyber_android.ui.screens.ftue_finish.model

import io.golos.cyber_android.ui.common.mvvm.model.ModelBaseImpl
import io.golos.domain.dto.FtueBoardStageDomain
import io.golos.domain.repositories.UsersRepository
import javax.inject.Inject

class FtueFinishModelImpl @Inject constructor(private val usersRepository: UsersRepository): FtueFinishModel, ModelBaseImpl() {

    override suspend fun setFtueBoardStage(stage: FtueBoardStageDomain) {
        usersRepository.setFtueBoardStage(stage)
    }
}