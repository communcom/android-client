package io.golos.cyber_android.ui.screens.post_report.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.golos.cyber_android.ui.screens.post_report.model.PostReportModel
import io.golos.cyber_android.ui.screens.post_report.view.PostReportDialog
import io.golos.cyber_android.ui.screens.post_report.view.view_commands.SendReportCommand
import io.golos.cyber_android.ui.shared.mvvm.viewModel.ViewModelBase
import io.golos.cyber_android.ui.shared.mvvm.view_commands.NavigateBackwardCommand
import io.golos.domain.DispatchersProvider
import kotlinx.coroutines.launch
import javax.inject.Inject

class PostReportViewModel @Inject constructor(
    dispatchersProvider: DispatchersProvider,
    model: PostReportModel
) : ViewModelBase<PostReportModel>(dispatchersProvider, model) {

    private val _isEnableSendButton: MutableLiveData<Boolean> = MutableLiveData(false)
    val isEnableSendButton: LiveData<Boolean> = _isEnableSendButton

    fun onSendClicked() {
        launch {
            _command.value = SendReportCommand(model.getReport())
        }
    }

    fun onClosedClicked() {
        _command.value = NavigateBackwardCommand()
    }

    fun collectReason(reasons: PostReportDialog.Type, reportString:String? = null) {
        model.collectReason(reasons, reportString)
        _isEnableSendButton.value = model.reasonsCount() > 0
    }
}