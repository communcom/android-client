package io.golos.cyber_android.ui.screens.post_report.view_model

import androidx.lifecycle.MutableLiveData
import io.golos.cyber_android.ui.shared.mvvm.viewModel.ViewModelBase
import io.golos.cyber_android.ui.shared.mvvm.view_commands.NavigateBackwardCommand
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

    val isEnableSendButton: MutableLiveData<Boolean> = MutableLiveData(false)

    fun onSendClicked() {
        launch {
            _command.value = SendReportCommand(model.getReport())
        }
    }

    fun onClosedClicked() {
        _command.value = NavigateBackwardCommand()
    }

    fun collectReason(reasons: PostReportDialog.Type) {
        model.collectReason(reasons)
        isEnableSendButton.value = model.reasonsCount() > 0
    }
}