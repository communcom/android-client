package io.golos.data.mappers

import io.golos.commun4j.services.model.ProposalChangeModel
import io.golos.domain.dto.ProposalChangeDomian

fun ProposalChangeModel.mapToProposalChangeDomain(): ProposalChangeDomian {
    return ProposalChangeDomian(
        new =  new?:"",
        old = old?:"",
        type = type?:""
    )
}