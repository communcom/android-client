package io.golos.cyber_android.ui.screens.post_report.view_model

import io.golos.cyber_android.ui.common.mvvm.viewModel.ViewModelBase
import io.golos.cyber_android.ui.common.mvvm.view_commands.BackCommand
import io.golos.cyber_android.ui.screens.post_report.model.PostReportModel
import io.golos.cyber_android.ui.screens.post_report.view.PostReportDialog
import io.golos.cyber_android.ui.screens.post_report.view.view_commands.SendReportCommand
import io.golos.domain.DispatchersProvider
import kotlinx.coroutines.launch
import javax.inject.Inject

class PostReportViewModel @Inject constructor(
    dispatchersProvider: DispatchersProvider,
    model: PostReportModel
) : ViewModelBase<PostReportModel>(dispatchersProvider, model) {

    fun onSendClicked() {
        launch {
            _command.value = SendReportCommand(model.getReport())
        }
    }

    fun onClosedClicked() {
        _command.value = BackCommand()
    }

    fun collectReason(reasons: PostReportDialog.Type) {
        model.collectReasons(reasons)
    }
}