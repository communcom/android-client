package io.golos.cyber_android.ui.screens.community_page_reports.fragments.reportcomment.model

import com.squareup.moshi.Moshi
import io.golos.commun4j.services.model.ReportRequestContentType
import io.golos.commun4j.services.model.ReportsRequestStatus
import io.golos.commun4j.services.model.ReportsRequestTimeSort
import io.golos.domain.dto.CommunityIdDomain
import io.golos.domain.dto.ReportedPostDomain
import io.golos.domain.use_cases.community.CommunitiesRepository
import javax.inject.Inject

class CommunityCommentReportsModelImpl
@Inject
constructor(
    private val communityIdDomain: CommunityIdDomain,
    private val communitiesRepository: CommunitiesRepository
) :CommunityCommentReportsModel{

    override suspend fun getCommentReports(limit:Int, offset:Int) =
        communitiesRepository.getCommunityReports(
            communityIdDomain,
            ReportRequestContentType.COMMENT,
            ReportsRequestStatus.OPEN,
            ReportsRequestTimeSort.TIME_DESC,
            limit,
            offset
        )

}