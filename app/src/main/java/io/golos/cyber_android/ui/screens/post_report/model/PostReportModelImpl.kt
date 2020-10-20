package io.golos.cyber_android.ui.screens.post_report.model

import android.annotation.SuppressLint
import io.golos.cyber_android.ui.screens.post_report.view.PostReportDialog
import io.golos.domain.dto.ContentIdDomain
import javax.inject.Inject

class PostReportModelImpl @Inject constructor(
    private val contentId: ContentIdDomain
) : PostReportModel {

    override fun getReport(): PostReportDialog.Report = PostReportDialog.Report(collectedReasons, contentId)

    private val collectedReasons = mutableListOf<String>()

    @SuppressLint("DefaultLocale")
    override fun collectReason(report: PostReportDialog.Type?,reportString: String?) {
        val lowerCaseReason = reportString ?: report?.name?.toLowerCase()
        if (!collectedReasons.contains(lowerCaseReason)) {
            if(report == PostReportDialog.Type.OTHER)
                collectedReasons.add("${report.name.toLowerCase()}-$reportString")
            else
                lowerCaseReason?.let { collectedReasons.add(it) }
        } else {
            collectedReasons.remove(lowerCaseReason)
        }
    }

    override fun reasonsCount(): Int {
        return collectedReasons.count()
    }

}