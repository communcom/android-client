package io.golos.cyber_android.ui.screens.community_page_reports.fragments.reportpost.model

import io.golos.cyber_android.ui.shared.mvvm.model.ModelBase
import io.golos.domain.dto.ReportedPostDomain

interface CommunityPostReportsModel : ModelBase {

    suspend fun getPostReports(limit: Int, offset: Int): List<ReportedPostDomain>

}