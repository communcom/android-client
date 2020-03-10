package io.golos.domain.dto

data class ConfigDomain(
    val ftueCommunityBonus: Int,
    val domain: String,
    val isNeedAppUpdate: Boolean
)