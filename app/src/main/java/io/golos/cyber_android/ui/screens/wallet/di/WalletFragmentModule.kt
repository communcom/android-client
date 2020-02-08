package io.golos.cyber_android.ui.screens.wallet.di

import dagger.Module
import dagger.Provides
import io.golos.domain.dependency_injection.Clarification
import io.golos.domain.dto.WalletCommunityBalanceRecordDomain
import javax.inject.Named

@Module
class WalletFragmentModule(private val pageSize: Int, private val balance: List<WalletCommunityBalanceRecordDomain>) {
    @Provides
    @Named(Clarification.PAGE_SIZE)
    fun providePageSize(): Int = pageSize

    @Provides
    fun provideBalance(): List<WalletCommunityBalanceRecordDomain> = balance
}