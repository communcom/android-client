package io.golos.data.mappers

import io.golos.commun4j.services.model.SubscriberItem
import io.golos.domain.dto.UserDomain
import io.golos.domain.dto.UserIdDomain

fun SubscriberItem.mapToUserDomain(): UserDomain =
    UserDomain(
        userId = UserIdDomain(userId.name),
        userName = username!!,
        userAvatar = avatarUrl,
        postsCount = postsCount ?: 0,
        followersCount = subscribersCount ?: 0
    )