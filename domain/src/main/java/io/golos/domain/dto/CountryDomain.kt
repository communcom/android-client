package io.golos.domain.dto

data class CountryDomain(
    val countryCode: String,
    val name: String,
    val code: Int,
    val available: Boolean,
    val emoji: String
)