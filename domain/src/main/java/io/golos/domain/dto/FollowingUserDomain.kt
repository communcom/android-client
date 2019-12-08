package io.golos.domain.dto

data class FollowingUserDomain(
    val user: UserDomain,
    /**
     * Is the user followed some other user (in most cases it's a current user)?
     */
    val isFollowed: Boolean
)