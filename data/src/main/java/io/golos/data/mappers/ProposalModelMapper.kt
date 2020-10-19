package io.golos.data.mappers

import io.golos.commun4j.services.model.ProposalModel
import io.golos.domain.dto.ProposalDomain
import io.golos.domain.dto.UserBriefDomain
import io.golos.domain.dto.UserIdDomain


fun ProposalModel.mapToProposalDomain(): ProposalDomain {
    return ProposalDomain(
        action =  action?:"",
        approvesCount = approvesCount?:0,
        approvesNeed = approvesNeed?:0,
        blockTime=blockTime?:"",
        change=change?.mapToProposalChangeDomain(),
        community=community.mapToCommunityDomain(),
        contract = contract?:"",
        contentType = contentType?:"",
        data=data?.mapToProposalDataDomain(),
        expiration = expiration?:"",
        isApproved = isApproved,
        permission = permission?:"",
        proposalId = proposalId?:"",
        proposer = proposer?.mapToAuthorDomain(),
        type = type?:""
    )
}