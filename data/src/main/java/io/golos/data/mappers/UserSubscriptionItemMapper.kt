package io.golos.data.mappers

import io.golos.commun4j.services.model.UserSubscriptionItem
import io.golos.domain.dto.UserDomain
import io.golos.domain.dto.UserIdDomain

fun UserSubscriptionItem.mapToUserDomain(): UserDomain =
    UserDomain(
        userId = UserIdDomain(userId.name),
        userName = username!!,
        userAvatar = avatarUrl,
        postsCount = postsCount,
        followersCount = subscribersCount
    )