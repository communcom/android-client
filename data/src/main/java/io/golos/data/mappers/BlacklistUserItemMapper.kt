package io.golos.data.mappers

import io.golos.commun4j.services.model.BlacklistUserItem
import io.golos.domain.dto.UserDomain
import io.golos.domain.dto.UserIdDomain

fun BlacklistUserItem.mapToUserDomain() : UserDomain =
    UserDomain(
        userId = UserIdDomain(userId.name),
        userName = username!!,
        userAvatar = avatarUrl,
        postsCount = 0,
        followersCount = 0,
        isSubscribed = isSubscribed ?: false
    )