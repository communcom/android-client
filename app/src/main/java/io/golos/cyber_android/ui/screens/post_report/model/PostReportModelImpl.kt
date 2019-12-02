package io.golos.cyber_android.ui.screens.post_report.model

import android.annotation.SuppressLint
import io.golos.cyber_android.ui.dto.Post
import io.golos.cyber_android.ui.screens.post_report.PostReportHolder
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PostReportModelImpl @Inject constructor(
    private val postReportHolder: PostReportHolder,
    private val contentId: Post.ContentId
) : PostReportModel {

    private val collectedReports = mutableListOf<String>()

    override suspend fun sendReports(reports: PostReportHolder.Report) {
        postReportHolder.sendReports(reports)
    }

    override val reportFlow: Flow<PostReportHolder.Report> = postReportHolder.reportFlow

    override fun getReports(): List<String> = collectedReports

    @SuppressLint("DefaultLocale")
    override fun collectReport(report: PostReportHolder.Type) {
        val lowerCaseReport = report.name.toLowerCase()
        if (!collectedReports.contains(lowerCaseReport)) {
            collectedReports.add(lowerCaseReport)
        } else {
            collectedReports.remove(lowerCaseReport)
        }
    }

    override fun getContentId(): Post.ContentId = contentId
}