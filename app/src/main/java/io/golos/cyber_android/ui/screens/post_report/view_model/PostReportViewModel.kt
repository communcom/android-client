package io.golos.cyber_android.ui.screens.post_report.view_model

import io.golos.cyber_android.ui.common.mvvm.viewModel.ViewModelBase
import io.golos.cyber_android.ui.common.mvvm.view_commands.BackCommand
import io.golos.cyber_android.ui.screens.post_report.PostReportHolder
import io.golos.cyber_android.ui.screens.post_report.model.PostReportModel
import io.golos.domain.DispatchersProvider
import kotlinx.coroutines.launch
import javax.inject.Inject

class PostReportViewModel @Inject constructor(
    dispatchersProvider: DispatchersProvider,
    model: PostReportModel
) : ViewModelBase<PostReportModel>(dispatchersProvider, model) {

    fun onSendClicked() {
        launch {
            model.sendReports(PostReportHolder.Report(model.getReports(), model.getContentId()))
            _command.value = BackCommand()
        }
    }

    fun onClosedClicked() {
        _command.value = BackCommand()
    }

    fun collectReport(report: PostReportHolder.Type) {
        model.collectReport(report)
    }
}