package io.golos.data.mappers

import io.golos.commun4j.services.model.NotificationUserDescription
import io.golos.domain.dto.UserIdDomain
import io.golos.domain.dto.notifications.UserNotificationDomain

fun NotificationUserDescription.mapToUserNotificationDomain() =
    UserNotificationDomain(
        id = UserIdDomain(this.userId.name),
        name = this.username,
        avatar = this.avatarUrl
    )