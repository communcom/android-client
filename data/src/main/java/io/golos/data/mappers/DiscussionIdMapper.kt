package io.golos.data.mappers

import io.golos.commun4j.model.DiscussionId
import io.golos.domain.dto.CommunityIdDomain
import io.golos.domain.dto.ContentIdDomain
import io.golos.domain.dto.UserIdDomain

fun DiscussionId.mapToContentIdDomain(): ContentIdDomain {
    return ContentIdDomain(
        CommunityIdDomain(this.communityId),
        this.permlink,
        UserIdDomain(this.userId.name)
    )
}