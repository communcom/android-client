package io.golos.data.mappers

import io.golos.commun4j.model.DiscussionId
import io.golos.domain.dto.ContentIdDomain

fun DiscussionId.mapToContentIdDomain(): ContentIdDomain {
    return ContentIdDomain(
        this.communityId,
        this.permlink,
        this.userId.name
    )
}