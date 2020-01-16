package io.golos.domain.dto

data class FollowingUserDomain(
    val user: UserDomain,

    val isBlocked: Boolean,

    val isInBlacklist: Boolean
)