package io.golos.data.mappers

import io.golos.commun4j.services.model.SubscriberItem
import io.golos.domain.dto.UserDomain

fun SubscriberItem.mapToUserDomain(): UserDomain =
    UserDomain(
    userId = userId.name,
    userName: String,
    userAvatar: String?,
    postsCount: Int?,
    followersCount: Int?
    )