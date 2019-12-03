package io.golos.cyber_android.ui.screens.post_report.model

import io.golos.cyber_android.ui.common.mvvm.model.ModelBase
import io.golos.cyber_android.ui.dto.Post
import io.golos.cyber_android.ui.screens.post_report.view.PostReportDialog
import kotlinx.coroutines.flow.Flow

interface PostReportModel: ModelBase {

    fun collectReason(report: PostReportDialog.Type)

    fun getReport(): PostReportDialog.Report
}