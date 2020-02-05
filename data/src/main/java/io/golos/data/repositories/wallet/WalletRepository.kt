package io.golos.data.repositories.wallet

interface WalletRepository {
    suspend fun getTotalBalanceInCommuns(): Double
}