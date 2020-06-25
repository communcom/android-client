package io.golos.data.mappers

import io.golos.commun4j.services.model.NotificationContentId
import io.golos.domain.dto.CommunityIdDomain
import io.golos.domain.dto.ContentIdDomain
import io.golos.domain.dto.UserIdDomain

fun NotificationContentId.mapToContentIdDomain() =
    ContentIdDomain(
        communityId = CommunityIdDomain(this.communityId!!.value),
        permlink = this.permlink,
        userId = UserIdDomain(this.userId.name)
)