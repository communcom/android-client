package io.golos.cyber_android.ui.screens.post_report.model

import io.golos.cyber_android.ui.shared.mvvm.model.ModelBase
import io.golos.cyber_android.ui.screens.post_report.view.PostReportDialog

interface PostReportModel: ModelBase {

    fun collectReason(report: PostReportDialog.Type)

    fun getReport(): PostReportDialog.Report
}