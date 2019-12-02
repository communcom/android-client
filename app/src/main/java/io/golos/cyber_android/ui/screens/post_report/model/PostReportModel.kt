package io.golos.cyber_android.ui.screens.post_report.model

import io.golos.cyber_android.ui.common.mvvm.model.ModelBase
import io.golos.cyber_android.ui.screens.post_report.PostReportHolder
import kotlinx.coroutines.flow.Flow

interface PostReportModel: ModelBase {

    suspend fun sendReports(reports: List<String>)

    fun collectReport(report: PostReportHolder.Type)

    fun getReports(): List<String>

    val reportFlow: Flow<PostReportHolder.Report>

}