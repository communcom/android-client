package io.golos.data.mappers

import io.golos.commun4j.model.DiscussionId
import io.golos.domain.dto.CommunityIdDomain
import io.golos.domain.dto.ContentIdDomain

fun DiscussionId.mapToContentIdDomain(): ContentIdDomain {
    return ContentIdDomain(
        CommunityIdDomain(this.communityId),
        this.permlink,
        this.userId.name
    )
}