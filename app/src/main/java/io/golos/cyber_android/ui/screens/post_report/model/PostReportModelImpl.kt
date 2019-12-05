package io.golos.cyber_android.ui.screens.post_report.model

import android.annotation.SuppressLint
import io.golos.cyber_android.ui.dto.Post
import io.golos.cyber_android.ui.screens.post_report.view.PostReportDialog
import javax.inject.Inject

class PostReportModelImpl @Inject constructor(
    private val contentId: Post.ContentId
) : PostReportModel {

    override fun getReport(): PostReportDialog.Report = PostReportDialog.Report(collectedReasons, contentId)

    private val collectedReasons = mutableListOf<String>()

    @SuppressLint("DefaultLocale")
    override fun collectReason(report: PostReportDialog.Type) {
        val lowerCaseReason = report.name.toLowerCase()
        if (!collectedReasons.contains(lowerCaseReason)) {
            collectedReasons.add(lowerCaseReason)
        } else {
            collectedReasons.remove(lowerCaseReason)
        }
    }
}