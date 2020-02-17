package io.golos.cyber_android.ui.screens.wallet_send_points.di

import dagger.Module
import dagger.Provides
import io.golos.domain.dependency_injection.Clarification
import io.golos.domain.dto.UserIdDomain
import io.golos.domain.dto.WalletCommunityBalanceRecordDomain
import javax.inject.Named

@Module
class WalletSendPointsFragmentModule(
    private val communityId: String,
    private val sendToUserId: UserIdDomain,
    private val balance: List<WalletCommunityBalanceRecordDomain>) {

    @Provides
    @Named(Clarification.COMMUNITY_ID)
    fun provideCommunityId(): String = communityId

    @Provides
    fun provideSendToUserId(): UserIdDomain = sendToUserId

    @Provides
    @Named(Clarification.WALLET_POINT_BALANCE)
    fun provideBalance(): List<WalletCommunityBalanceRecordDomain> = balance
}
