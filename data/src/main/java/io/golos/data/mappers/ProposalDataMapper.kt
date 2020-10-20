package io.golos.data.mappers

import io.golos.commun4j.services.model.ProposalDataModel
import io.golos.domain.dto.ProposalDataDomain


fun ProposalDataModel.mapToProposalDataDomain(): ProposalDataDomain {
    return ProposalDataDomain(
        avatar_image = avatar_image?:"",
        commun_code = commun_code?:"",
        cover_image = cover_image?:"",
        description = description?:""
    )
}