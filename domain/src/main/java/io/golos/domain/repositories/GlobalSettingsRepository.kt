package io.golos.domain.repositories

import io.golos.domain.dto.RewardCurrency
import kotlinx.coroutines.flow.Flow

interface GlobalSettingsRepository {
    val rewardCurrency: RewardCurrency

    val rewardCurrencyUpdates: Flow<RewardCurrency?>

    suspend fun loadValues()

    suspend fun updateRewardCurrency(currency: RewardCurrency)

    val isBalanceUpdated: Flow<Boolean?>

    suspend fun notifyBalanceUpdate(isBalanceUpdated: Boolean?)

    val isCurrencyUpdated: Flow<Boolean?>

    suspend fun notifyCurrencyUpdate(isCurrencyUpdated: Boolean?)
}