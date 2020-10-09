package io.golos.cyber_android.ui.screens.community_page_reports.fragments.reportcomment.model

import io.golos.cyber_android.ui.shared.mvvm.model.ModelBase
import io.golos.domain.dto.ReportedPostDomain

interface CommunityCommentReportsModel : ModelBase {

    suspend fun getCommentReports(limit: Int, offset: Int): List<ReportedPostDomain>

}