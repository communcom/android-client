package io.golos.cyber_android.ui.screens.donate_send_points.model

import android.content.Context
import io.golos.cyber_android.ui.dto.PostDonation
import io.golos.cyber_android.ui.screens.wallet_send_points.model.WalletSendPointsModel
import io.golos.cyber_android.ui.screens.wallet_send_points.model.WalletSendPointsModelImpl
import io.golos.cyber_android.ui.screens.wallet_shared.amount_validator.AmountValidator
import io.golos.cyber_android.ui.shared.broadcast_actions_registries.PostUpdateRegistry
import io.golos.data.repositories.wallet.WalletRepository
import io.golos.domain.GlobalConstants
import io.golos.domain.dependency_injection.Clarification
import io.golos.domain.dto.CommunityIdDomain
import io.golos.domain.dto.ContentIdDomain
import io.golos.domain.dto.UserDomain
import io.golos.domain.dto.WalletCommunityBalanceRecordDomain
import io.golos.domain.repositories.GlobalSettingsRepository
import javax.inject.Inject
import javax.inject.Named

class DonateSendPointsModelImpl
@Inject
constructor(
    appContext: Context,
    sendToUser: UserDomain?,
    currentCommunityId: CommunityIdDomain,
    @Named(Clarification.WALLET_POINT_BALANCE)
    balance: List<WalletCommunityBalanceRecordDomain>,
    walletRepository: WalletRepository,
    amountValidator: AmountValidator,
    @Named(Clarification.AMOUNT)
    override var amount: Double?,
    private val postId: ContentIdDomain,
    private val postUpdateRegistry: PostUpdateRegistry,
    globalSettingsRepository: GlobalSettingsRepository
) : WalletSendPointsModelImpl(
    appContext,
    sendToUser,
    currentCommunityId,
    balance,
    walletRepository,
    amountValidator,
    globalSettingsRepository
), WalletSendPointsModel {

    override val canSelectUser = false

    override val titleTextResId: Int = io.golos.cyber_android.R.string.donate_title

    override fun initCurrentBalanceRecord(): WalletCommunityBalanceRecordDomain =
        balance.firstOrNull { it.communityId == currentCommunityId && it.points != 0.0 } ?: run {
            currentCommunityId = CommunityIdDomain(GlobalConstants.COMMUN_CODE)
            initCurrentBalanceRecord()
        }

    override suspend fun makeTransfer() {
        val serverDonation = walletRepository.donate(postId, amount!!, currentCommunityId)
        if(serverDonation != null) {
            postUpdateRegistry.setDonationSend(PostDonation(postId, serverDonation))
        }
    }
}