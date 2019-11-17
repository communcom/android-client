package io.golos.data.mappers

import io.golos.commun4j.model.DiscussionMetadata
import io.golos.domain.dto.PostDomain

fun DiscussionMetadata.mapToMetaDomain(): PostDomain.MetaDomain {
    return PostDomain.MetaDomain(this.creationTime)
}