package io.golos.domain.dto

data class FcmTokenStateDomain(
    val sent: Boolean,
    val token: String
)