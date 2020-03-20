package io.golos.domain.dto

data class SignUpIdentityDomain (
    val oauthState: String?,
    val identity: String,
    val provider: String
)