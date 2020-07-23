package io.golos.data.mappers

import io.golos.commun4j.model.DiscussionMetadata
import io.golos.domain.dto.MetaDomain

fun DiscussionMetadata.mapToMetaDomain(): MetaDomain {
    return MetaDomain(this.creationTime,this.trxId)
}