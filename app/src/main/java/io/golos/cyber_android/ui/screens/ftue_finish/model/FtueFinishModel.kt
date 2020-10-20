package io.golos.cyber_android.ui.screens.ftue_finish.model

import io.golos.cyber_android.ui.shared.mvvm.model.ModelBase
import io.golos.domain.dto.FtueBoardStageDomain

interface FtueFinishModel: ModelBase {

    suspend fun setFtueBoardStage(stage: FtueBoardStageDomain)
}