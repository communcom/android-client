package io.golos.domain.dto

data class FollowingUserDomain(
    val user: UserDomain,
    /**
     * Is the user subscribed to a current user
     */
    val isSubscribed: Boolean,

    val isBlocked: Boolean,

    val isInBlacklist: Boolean
)