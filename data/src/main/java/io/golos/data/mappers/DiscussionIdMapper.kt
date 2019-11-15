package io.golos.data.mappers

import io.golos.commun4j.model.DiscussionId
import io.golos.domain.dto.PostDomain

fun DiscussionId.mapToContentIdDomain(): PostDomain.ContentIdDomain {
    return PostDomain.ContentIdDomain(
        this.communityId,
        this.permlink,
        this.userId.name
    )
}