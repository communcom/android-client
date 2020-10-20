package io.golos.domain.dto


data class ProposalDomain (
    val action: String,
    val approvesCount: Int,
    val approvesNeed: Int,
    val blockTime: String,
    val contract: String,
    val contentType: String,
    val data: ProposalDataDomain?,
    val change: ProposalChangeDomian?,
    val community: CommunityDomain?,
    val isApproved: Boolean,
    val expiration: String,
    val permission: String,
    val proposalId: String,
    val proposer: UserBriefDomain?,
    val type: String
)