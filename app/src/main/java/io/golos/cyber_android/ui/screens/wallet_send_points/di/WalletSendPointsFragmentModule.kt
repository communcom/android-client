package io.golos.cyber_android.ui.screens.wallet_send_points.di

import dagger.Module
import dagger.Provides
import io.golos.domain.dependency_injection.Clarification
import io.golos.domain.dto.UserDomain
import io.golos.domain.dto.WalletCommunityBalanceRecordDomain
import javax.inject.Named

@Module
class WalletSendPointsFragmentModule(
    private val communityId: String,
    private val sendToUser: UserDomain?,
    private val balance: List<WalletCommunityBalanceRecordDomain>) {

    @Provides
    @Named(Clarification.COMMUNITY_ID)
    fun provideCommunityId(): String = communityId

    @Provides
    fun provideSendToUserId(): UserDomain? = sendToUser

    @Provides
    @Named(Clarification.WALLET_POINT_BALANCE)
    fun provideBalance(): List<WalletCommunityBalanceRecordDomain> = balance
}
