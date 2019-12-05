package io.golos.data.mappers

import io.golos.commun4j.services.model.GetProfileResult
import io.golos.domain.dto.UserDomain
import io.golos.domain.dto.UserIdDomain

fun GetProfileResult.ProfileCommonFriendItem.mapToUserDomain(): UserDomain =
    UserDomain(
        userId = UserIdDomain(userId.name),
        userName = username!!,
        userAvatar = avatarUrl,
        postsCount = 0,
        followersCount = subscribersCount
    )