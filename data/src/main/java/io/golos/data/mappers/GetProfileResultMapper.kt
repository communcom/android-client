package io.golos.data.mappers

import io.golos.commun4j.services.model.GetProfileResult
import io.golos.domain.dto.UserProfileDomain

fun GetProfileResult.mapToUserProfileDomain(): UserProfileDomain {
    return UserProfileDomain(
        userId = userId,
        coverUrl = personal?.coverUrl,
        avatarUrl = personal?.avatarUrl,
        bio = personal?.biography,
        name = username!!,
        joinDate = registration!!.time,
        followersCount = subscribers?.usersCount?.toLong() ?: 0L,
        followingsCount = subscriptions?.usersCount?.toLong() ?: 0L
    )
}