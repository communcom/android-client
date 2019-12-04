package io.golos.domain.dto

data class UserDomain(
    val userId: CyberUser, UserIdDomain
    val userName: String,
    val userAvatar: String?,
    val postsCount: Int?,
    val followersCount: Int?
)