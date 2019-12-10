package io.golos.cyber_android.ui.screens.ftue.view_model

import io.golos.cyber_android.ui.common.mvvm.viewModel.ViewModelBase
import io.golos.cyber_android.ui.screens.ftue.model.FtueModel
import io.golos.cyber_android.ui.screens.ftue.view.view_command.FtueDoneCommand
import io.golos.cyber_android.ui.screens.ftue.view.view_command.FtuePage
import io.golos.cyber_android.ui.screens.ftue.view.view_command.NavigateToFtuePageCommand
import io.golos.domain.DispatchersProvider
import io.golos.domain.dto.FtueBoardStageDomain
import kotlinx.coroutines.launch
import javax.inject.Inject

class FtueViewModel @Inject constructor(dispatchersProvider: DispatchersProvider, ftueModel: FtueModel) :
    ViewModelBase<FtueModel>(dispatchersProvider, ftueModel) {

    fun startToFirstScreen() {
        launch {
            when(model.getFtueBoardStage()){
                FtueBoardStageDomain.NEED_SHOW -> _command.value = NavigateToFtuePageCommand(FtuePage.SEARCH_COMMUNITIES)
                FtueBoardStageDomain.SEARCH_COMMUNITIES -> _command.value = NavigateToFtuePageCommand(FtuePage.SEARCH_COMMUNITIES)
                FtueBoardStageDomain.FINISH -> _command.value = NavigateToFtuePageCommand(FtuePage.FINISH)
                else -> _command.value = FtueDoneCommand()
            }
        }
    }

    fun onSkipClicked() {
        launch {
            model.setFtueBoardStage(FtueBoardStageDomain.PASSED)
            _command.value = FtueDoneCommand()
        }
    }
}