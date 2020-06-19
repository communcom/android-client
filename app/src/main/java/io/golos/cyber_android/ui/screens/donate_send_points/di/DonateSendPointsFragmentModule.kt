package io.golos.cyber_android.ui.screens.donate_send_points.di

import dagger.Module
import dagger.Provides
import io.golos.domain.dependency_injection.Clarification
import io.golos.domain.dto.CommunityIdDomain
import io.golos.domain.dto.ContentIdDomain
import io.golos.domain.dto.UserDomain
import io.golos.domain.dto.WalletCommunityBalanceRecordDomain
import javax.inject.Named

@Module
class DonateSendPointsFragmentModule(
    private val postId: ContentIdDomain,
    private val communityId: CommunityIdDomain,
    private val sendToUser: UserDomain,
    private val balance: List<WalletCommunityBalanceRecordDomain>,
    private val amount: Double?) {

    @Provides
    fun providePostId(): ContentIdDomain = postId

    @Provides
    fun provideCommunityId(): CommunityIdDomain = communityId

    @Provides
    fun provideSendToUserId(): UserDomain? = sendToUser

    @Provides
    @Named(Clarification.WALLET_POINT_BALANCE)
    fun provideBalance(): List<WalletCommunityBalanceRecordDomain> = balance

    @Provides
    @Named(Clarification.AMOUNT)
    fun provideAmount(): Double? = amount
}
