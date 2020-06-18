package io.golos.cyber_android.ui.screens.donate_send_points.model

import android.content.Context
import io.golos.cyber_android.ui.screens.wallet_send_points.model.WalletSendPointsModel
import io.golos.cyber_android.ui.screens.wallet_send_points.model.WalletSendPointsModelImpl
import io.golos.cyber_android.ui.screens.wallet_shared.amount_validator.AmountValidator
import io.golos.data.repositories.wallet.WalletRepository
import io.golos.domain.GlobalConstants
import io.golos.domain.dependency_injection.Clarification
import io.golos.domain.dto.CommunityIdDomain
import io.golos.domain.dto.UserDomain
import io.golos.domain.dto.WalletCommunityBalanceRecordDomain
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
    amountValidator: AmountValidator
) : WalletSendPointsModelImpl(
        appContext,
        sendToUser,
        currentCommunityId,
        balance,
        walletRepository,
        amountValidator
), WalletSendPointsModel {

    override fun initCurrentBalanceRecord(): WalletCommunityBalanceRecordDomain =
        balance.firstOrNull { it.communityId == currentCommunityId } ?: run {
            currentCommunityId = CommunityIdDomain(GlobalConstants.COMMUN_CODE)
            initCurrentBalanceRecord()
        }
}