package io.golos.cyber_android.ui.screens.ftue_finish.view_model

import io.golos.cyber_android.R
import io.golos.cyber_android.ui.screens.ftue_finish.model.FtueFinishModel
import io.golos.cyber_android.ui.shared.mvvm.viewModel.ViewModelBase
import io.golos.cyber_android.ui.shared.mvvm.view_commands.NavigationCommand
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
            _command.value = NavigationCommand(R.id.action_ftueFragment_to_dashboardFragment)
        }
    }

    init {
        launch {
            model.setFtueBoardStage(FtueBoardStageDomain.FINISH)
        }
    }
}