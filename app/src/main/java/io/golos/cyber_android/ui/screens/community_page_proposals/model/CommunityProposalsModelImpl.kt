package io.golos.cyber_android.ui.screens.community_page_proposals.model

import io.golos.domain.dto.CommunityIdDomain
import io.golos.domain.dto.ProposalDomain
import io.golos.domain.use_cases.community.CommunitiesRepository
import javax.inject.Inject

class CommunityProposalsModelImpl
@Inject
constructor(
    private val communityIdDomain: CommunityIdDomain,
    private val communityRepository: CommunitiesRepository
) : CommunityProposalsModel {

    override suspend fun getProposals( limit: Int, offset: Int):List<ProposalDomain> =communityRepository.getProposals(communityIdDomain, limit, offset)

}