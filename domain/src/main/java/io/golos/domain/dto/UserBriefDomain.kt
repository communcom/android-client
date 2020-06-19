package io.golos.domain.dto

data class UserBriefDomain(
    val avatarUrl: String?,
    val userId: UserIdDomain,
    val username: String?
)