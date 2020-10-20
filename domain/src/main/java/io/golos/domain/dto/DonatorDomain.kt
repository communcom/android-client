package io.golos.domain.dto

data class DonatorDomain (
    val person: UserBriefDomain,
    val amount: String
)