package io.golos.cyber_android.ui.screens.community_page_proposals.model

import io.golos.cyber_android.ui.shared.mvvm.model.ModelBase
import io.golos.domain.dto.CommunityIdDomain
import io.golos.domain.dto.ProposalDomain

interface CommunityProposalsModel : ModelBase {
    suspend fun getProposals( limit:Int, offset:Int):List<ProposalDomain>
}