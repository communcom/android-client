package io.golos.cyber_android.ui.screens.community_page_reports.model

import com.squareup.moshi.Moshi
import io.golos.commun4j.services.model.ReportRequestContentType
import io.golos.commun4j.services.model.ReportsRequestStatus
import io.golos.commun4j.services.model.ReportsRequestTimeSort
import io.golos.domain.dto.CommunityIdDomain
import io.golos.domain.dto.ReportedPostDomain
import io.golos.domain.use_cases.community.CommunitiesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

class CommunityReportsModelImpl
@Inject
constructor(
    private val moshi: Moshi,
    private val communityIdDomain: CommunityIdDomain,
    private val communitiesRepository: CommunitiesRepository
) :CommunityReportsModel{

    override suspend fun getPostReports(limit:Int, offset:Int): List<ReportedPostDomain>{
        val response = communitiesRepository.getCommunityReports(
            communityIdDomain,
            ReportRequestContentType.POST,
            ReportsRequestStatus.OPEN,
            ReportsRequestTimeSort.TIME_DESC,
            limit,
            offset
        )
        getEntityReports(response,limit,offset)
        return response
    }

    private suspend fun getEntityReports(response: List<ReportedPostDomain>,limit:Int, offset:Int) {
        coroutineScope {
            response.map {
                async(Dispatchers.IO) {
                    it.entityReport= communitiesRepository.getEntityReports(it.post.community.communityId,it.post.contentId.userId,it.post.contentId.permlink,limit,offset)
                }
            }.awaitAll()
        }
    }

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