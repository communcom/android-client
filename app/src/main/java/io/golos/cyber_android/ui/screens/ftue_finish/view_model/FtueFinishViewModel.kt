package io.golos.cyber_android.ui.screens.ftue_finish.view_model

import io.golos.cyber_android.ui.common.mvvm.viewModel.ViewModelBase
import io.golos.cyber_android.ui.screens.ftue_finish.model.FtueFinishModel
import io.golos.cyber_android.ui.screens.ftue_finish.view.view_command.FtueFinishCommand
import io.golos.domain.DispatchersProvider
import io.golos.domain.dto.FtueBoardStageDomain
import kotlinx.coroutines.launch
import javax.inject.Inject

class FtueFinishViewModel @Inject constructor(
    dispatchersProvider: DispatchersProvider,
    ftueFinishModel: FtueFinishModel
) : ViewModelBase<FtueFinishModel>(dispatchersProvider, ftueFinishModel) {

    fun onDoneClicked() {
        launch {
            model.setFtueBoardStage(FtueBoardStageDomain.PASSED)
            _command.value = FtueFinishCommand()
        }
    }

    init {
        launch {
            model.setFtueBoardStage(FtueBoardStageDomain.FINISH)
        }
    }
}