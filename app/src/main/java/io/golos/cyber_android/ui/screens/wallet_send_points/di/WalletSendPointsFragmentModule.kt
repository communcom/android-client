package io.golos.cyber_android.ui.screens.wallet_send_points.di

import dagger.Module
import dagger.Provides
import io.golos.domain.dependency_injection.Clarification
import io.golos.domain.dto.CommunityIdDomain
import io.golos.domain.dto.UserBriefDomain
import io.golos.domain.dto.WalletCommunityBalanceRecordDomain
import javax.inject.Named

@Module
class WalletSendPointsFragmentModule(
    private val communityId: CommunityIdDomain,
    private val sendToUser: UserBriefDomain?,
    private val balance: List<WalletCommunityBalanceRecordDomain>) {

    @Provides
    fun provideCommunityId(): CommunityIdDomain = communityId

    @Provides
    fun provideSendToUserId(): UserBriefDomain? = sendToUser

    @Provides
    @Named(Clarification.WALLET_POINT_BALANCE)
    fun provideBalance(): List<WalletCommunityBalanceRecordDomain> = balance
}
