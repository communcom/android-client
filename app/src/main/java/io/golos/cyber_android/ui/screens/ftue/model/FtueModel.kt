package io.golos.cyber_android.ui.screens.ftue.model

import io.golos.cyber_android.ui.common.mvvm.model.ModelBase
import io.golos.domain.dto.FtueBoardStageDomain

interface FtueModel : ModelBase {

    suspend fun setFtueBoardStage(stage: FtueBoardStageDomain)

    suspend fun getFtueBoardStage(): FtueBoardStageDomain
}