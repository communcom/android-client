package io.golos.cyber_android.ui.screens.wallet.di

import dagger.Module
import dagger.Provides
import io.golos.domain.dependency_injection.Clarification
import io.golos.domain.dto.WalletCommunityBalanceRecordDomain
import javax.inject.Named

@Module
class WalletFragmentModule(private val balance: List<WalletCommunityBalanceRecordDomain>) {
    @Provides
    fun provideBalance(): List<WalletCommunityBalanceRecordDomain> = balance
}