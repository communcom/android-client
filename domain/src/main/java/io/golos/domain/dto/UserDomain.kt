package io.golos.domain.dto

data class UserDomain(
        val userId: UserIdDomain,
        val userName: String,
        val userAvatar: String?,
        val postsCount: Int?,
        val followersCount: Int?
)