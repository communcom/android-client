package io.golos.cyber_android.ui.screens.community_page_reports.model

import io.golos.commun4j.services.model.GetReportsResponse
import io.golos.cyber_android.ui.shared.mvvm.model.ModelBase
import io.golos.domain.dto.ReportedPostDomain

interface CommunityReportsModel : ModelBase {

    suspend fun getPostReports(limit: Int, offset: Int): List<ReportedPostDomain>

    suspend fun getCommentReports(limit: Int, offset: Int):List<ReportedPostDomain>

}