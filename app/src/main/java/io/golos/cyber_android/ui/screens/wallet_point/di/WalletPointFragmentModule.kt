package io.golos.cyber_android.ui.screens.wallet_point.di

import dagger.Module
import dagger.Provides
import io.golos.domain.dependency_injection.Clarification
import io.golos.domain.dependency_injection.scopes.SubFragmentScope
import io.golos.domain.dto.WalletCommunityBalanceRecordDomain
import javax.inject.Named

@Module
class WalletPointFragmentModule(
    private val communityId: String,
    private val balance: List<WalletCommunityBalanceRecordDomain>) {

    @Provides
    @Named(Clarification.COMMUNITY_ID)
    fun provideCommunityId(): String = communityId

    @Provides
    @Named(Clarification.WALLET_POINT_BALANCE)
    fun provideBalance(): List<WalletCommunityBalanceRecordDomain> = balance
}
