package io.golos.data.mappers

import io.golos.commun4j.services.model.NotificationEntityContent
import io.golos.domain.dto.ContentIdDomain
import io.golos.domain.dto.PostNotificationDomain

fun NotificationEntityContent.mapToPostNotificationDomain(): PostNotificationDomain {
    val contentIdDomain = ContentIdDomain(contentId.communityId?.value ?: "", this.contentId.permlink, contentId.userId.name)
    return PostNotificationDomain(contentIdDomain, shortText, imageUrl)
}