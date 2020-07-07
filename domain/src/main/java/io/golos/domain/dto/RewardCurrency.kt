package io.golos.domain.dto

enum class RewardCurrency {
    POINTS,
    COMMUNS,
    USD;

    companion object {
        fun createFrom(value: String): RewardCurrency = RewardCurrency.valueOf(value)
    }
}