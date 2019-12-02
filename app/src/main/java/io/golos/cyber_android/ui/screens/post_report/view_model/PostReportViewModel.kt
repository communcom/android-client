package io.golos.cyber_android.ui.screens.post_report.view_model

import androidx.lifecycle.MutableLiveData
import io.golos.cyber_android.ui.common.mvvm.viewModel.ViewModelBase
import io.golos.cyber_android.ui.common.mvvm.view_commands.BackCommand
import io.golos.cyber_android.ui.screens.post_report.PostReportHolder
import io.golos.cyber_android.ui.screens.post_report.model.PostReportModel
import io.golos.cyber_android.ui.utils.toLiveData
import io.golos.domain.DispatchersProvider
import kotlinx.coroutines.launch
import javax.inject.Inject

class PostReportViewModel @Inject constructor(
    dispatchersProvider: DispatchersProvider,
    model: PostReportModel
) : ViewModelBase<PostReportModel>(dispatchersProvider, model) {

    private val _reportType = MutableLiveData<List<PostReportHolder.Type>>()

    val reportType = _reportType.toLiveData()

    fun onSendClicked() {
        val reports = model.getReports()
        launch {
            model.sendReports(reports)
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