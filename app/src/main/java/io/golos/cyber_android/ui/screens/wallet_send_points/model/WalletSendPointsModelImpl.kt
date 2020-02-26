package io.golos.cyber_android.ui.screens.wallet_send_points.model

import android.content.Context
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.screens.wallet_dialogs.transfer_completed.TransferCompletedInfo
import io.golos.cyber_android.ui.screens.wallet_point.dto.CarouselStartData
import io.golos.cyber_android.ui.screens.wallet_shared.amount_validator.AmountValidator
import io.golos.cyber_android.ui.screens.wallet_shared.dto.AmountValidationResult
import io.golos.cyber_android.ui.screens.wallet_shared.carousel.CarouselListItem
import io.golos.cyber_android.ui.shared.mvvm.model.ModelBaseImpl
import io.golos.data.repositories.wallet.WalletRepository
import io.golos.domain.GlobalConstants
import io.golos.domain.dependency_injection.Clarification
import io.golos.domain.dto.UserDomain
import io.golos.domain.dto.WalletCommunityBalanceRecordDomain
import io.golos.utils.capitalize
import timber.log.Timber
import java.util.*
import javax.inject.Inject
import javax.inject.Named

class WalletSendPointsModelImpl
@Inject
constructor(
    private val appContext: Context,
    override var sendToUser: UserDomain?,
    @Named(Clarification.COMMUNITY_ID)
    private var currentCommunityId: String,
    @Named(Clarification.WALLET_POINT_BALANCE)
    override var balance: List<WalletCommunityBalanceRecordDomain>,
    private val walletRepository: WalletRepository,
    private val amountValidator: AmountValidator
) : ModelBaseImpl(), WalletSendPointsModel {

    private var amount: Double? = null

    private val communName: String by lazy { appContext.getString(R.string.commun).capitalize(Locale.getDefault()) }

    init {
        // Move Commun community to the first
        balance =
            mutableListOf(
                balance.first { it.communityId == GlobalConstants.COMMUN_CODE }
                    .copy(communityName = communName)
            )
            .also {
                it.addAll(balance.filter { it.communityId != GlobalConstants.COMMUN_CODE })
            }
    }

    override var currentBalanceRecord: WalletCommunityBalanceRecordDomain = calculateCurrentBalanceRecord()

    override val carouselItemsData: CarouselStartData = CarouselStartData(
        startIndex = balance.indexOfFirst { it.communityId == currentCommunityId },
        items = balance.map { CarouselListItem(id = it.communityId, iconUrl = it.communityLogoUrl) }
    )

    override val hasFee: Boolean
        get() = currentBalanceRecord.communityId != GlobalConstants.COMMUN_CODE

    override fun updateAmount(amountAsString: String?): Boolean =
        try {
            amount = if(amountAsString.isNullOrBlank()) null else amountAsString.toDouble()
            true
        } catch (ex: NumberFormatException) {
            Timber.e(ex)
            amount = null
            false
        }

    /**
     * @return Index of the community in the balance list
     */
    override fun updateCurrentCommunity(communityId: String): Int? {
        if(communityId == currentCommunityId) {
            return null
        }

        currentCommunityId = communityId
        currentBalanceRecord = calculateCurrentBalanceRecord()
        return balance.indexOf(currentBalanceRecord)
    }

    override fun validateAmount(): AmountValidationResult {
        val fee = if(hasFee) currentBalanceRecord.points/1000 else 0.0
        return amountValidator.validate(amount, currentBalanceRecord.points, fee)
    }

    override suspend fun makeTransfer() = walletRepository.makeTransfer(sendToUser!!.userId, amount!!, currentCommunityId)

    override fun getTransferCompletedInfo(): TransferCompletedInfo {
        val pointsName = if(currentCommunityId != GlobalConstants.COMMUN_CODE) {
            currentBalanceRecord.communityName ?: currentBalanceRecord.communityId
        } else {
            communName
        }

        return TransferCompletedInfo(
            date = Date(),
            amountTransfered = amount!!,
            amountRemain = currentBalanceRecord.points - calculateFee() - amount!!,
            userLogoUrl = sendToUser!!.userAvatar,
            userName = sendToUser!!.userName,
            pointsLogoUrl = currentBalanceRecord.communityLogoUrl,
            pointsName = pointsName,
            showFee = hasFee
        )
    }

    private fun calculateCurrentBalanceRecord() = balance.first { it.communityId == currentCommunityId }

    private fun calculateFee(): Double = if(hasFee) currentBalanceRecord.points/1000 else 0.0
}