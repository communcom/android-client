package io.golos.cyber_android.ui.screens.post_view.dto

import io.golos.domain.dto.RewardCurrency

data class RewardInfo(
    val rewardValueInPoints: Double?,
    val rewardValueInCommun: Double?,
    val rewardValueInUSD: Double?,
    val rewardCurrency: RewardCurrency
)