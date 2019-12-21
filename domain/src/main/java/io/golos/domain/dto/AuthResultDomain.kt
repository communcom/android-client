package io.golos.domain.dto

data class AuthResultDomain(
    val permission: String,
    val user: String,
    val userId: UserIdDomain,
    val username: String?
)