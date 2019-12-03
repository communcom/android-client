package io.golos.cyber_android.ui.screens.post_report.model

import android.annotation.SuppressLint
import io.golos.cyber_android.ui.dto.Post
import io.golos.cyber_android.ui.screens.post_report.view.PostReportDialog
import javax.inject.Inject

class PostReportModelImpl @Inject constructor(
    private val contentId: Post.ContentId
) : PostReportModel {

    override fun getReport(): PostReportDialog.Report = PostReportDialog.Report(collectedReports, contentId)

    private val collectedReports = mutableListOf<String>()

    @SuppressLint("DefaultLocale")
    override fun collectReasons(report: PostReportDialog.Type) {
        val lowerCaseReport = report.name.toLowerCase()
        if (!collectedReports.contains(lowerCaseReport)) {
            collectedReports.add(lowerCaseReport)
        } else {
            collectedReports.remove(lowerCaseReport)
        }
    }
}