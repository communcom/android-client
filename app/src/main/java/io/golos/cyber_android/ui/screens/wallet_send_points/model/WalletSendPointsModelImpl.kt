package io.golos.cyber_android.ui.screens.wallet_send_points.model

import android.content.Context
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.screens.wallet_dialogs.transfer_completed.TransferCompletedInfo
import io.golos.cyber_android.ui.screens.wallet_point.dto.CarouselStartData
import io.golos.cyber_android.ui.screens.wallet_shared.amount_validator.AmountValidator
import io.golos.cyber_android.ui.screens.wallet_shared.carousel.CarouselListItem
import io.golos.cyber_android.ui.screens.wallet_shared.dto.AmountValidationResult
import io.golos.cyber_android.ui.shared.mvvm.model.ModelBaseImpl
import io.golos.data.repositories.wallet.WalletRepository
import io.golos.domain.GlobalConstants
import io.golos.domain.dependency_injection.Clarification
import io.golos.domain.dto.CommunityIdDomain
import io.golos.domain.dto.UserBriefDomain
import io.golos.domain.dto.WalletCommunityBalanceRecordDomain
import io.golos.domain.repositories.GlobalSettingsRepository
import io.golos.utils.helpers.capitalize
import timber.log.Timber
import java.util.*
import javax.inject.Inject
import javax.inject.Named
import kotlin.collections.ArrayList

@Suppress("LeakingThis")
open class WalletSendPointsModelImpl
@Inject
constructor(
    private val appContext: Context,
    override var sendToUser: UserBriefDomain?,
    protected var currentCommunityId: CommunityIdDomain,
    @Named(Clarification.WALLET_POINT_BALANCE)
    override var balance: List<WalletCommunityBalanceRecordDomain>,
    protected val walletRepository: WalletRepository,
    private val amountValidator: AmountValidator,
    private val globalSettingsRepository: GlobalSettingsRepository
) : ModelBaseImpl(), WalletSendPointsModel {
    override val canSelectUser = true

    protected open var amount: Double? = null

    private val communName: String by lazy { appContext.getString(R.string.commun).capitalize(Locale.getDefault()) }

    init {
        // Move Commun community to the first
        val allBalances = ArrayList<WalletCommunityBalanceRecordDomain>()
        allBalances.addAll(balance)
        allBalances.forEachIndexed { index, item ->
            if(item.communityId.code == GlobalConstants.COMMUN_CODE){
                val tmp = allBalances[0]
                allBalances[0] = item
                allBalances[index] = tmp
                return@forEachIndexed
            }
        }
        balance = allBalances.filter { it.points > 0  || it.communityId == CommunityIdDomain(GlobalConstants.COMMUN_CODE) }
        /*balance =
            mutableListOf(balance.first { it.communityId.code == GlobalConstants.COMMUN_CODE }.copy(communityName = communName)).also {
                it.addAll(balance.filter { it1->
                    it1.points > 0 })
            }*/
    }

    override var currentBalanceRecord: WalletCommunityBalanceRecordDomain = initCurrentBalanceRecord()

    override val carouselItemsData: CarouselStartData =
        CarouselStartData(
            startIndex = balance.indexOfFirst { it.communityId == currentCommunityId },
            items = balance.map { CarouselListItem(id = it.communityId, iconUrl = it.communityLogoUrl) }
        )

    override val hasFee: Boolean
        get() = currentBalanceRecord.communityId.code != GlobalConstants.COMMUN_CODE

    override val titleTextResId: Int = R.string.send_points

    override fun updateAmount(amountAsString: String?): Boolean = try {
        amount = if (amountAsString.isNullOrBlank()) null else amountAsString.toDouble()
        true
    } catch (ex: NumberFormatException) {
        Timber.e(ex)
        amount = null
        false
    }

    /**
     * @return Index of the community in the balance list
     */
    override fun updateCurrentCommunity(communityId: CommunityIdDomain): Pair<Int?,Double?>? {
        if (communityId == currentCommunityId) {
            return null
        }

        currentCommunityId = communityId
        currentBalanceRecord = calculateCurrentBalanceRecord()
        return Pair(balance.indexOf(currentBalanceRecord),currentBalanceRecord.points)
    }

    override fun validateAmount(): AmountValidationResult {
        val fee = if (hasFee) currentBalanceRecord.points / 1000 else 0.0
        return amountValidator.validate(amount, currentBalanceRecord.points, fee)
    }

    override suspend fun makeTransfer() = walletRepository.sendToUser(sendToUser!!.userId, amount!!, currentCommunityId)

    override suspend fun notifyBalanceUpdate(isBalanceUpdated: Boolean) {
        globalSettingsRepository.notifyBalanceUpdate(isBalanceUpdated)
    }

    override suspend fun updateBalances():Pair<Int?,Double?>?{
        val newBalances = walletRepository.getBalance()
        val allBalances = ArrayList<WalletCommunityBalanceRecordDomain>()
        allBalances.addAll(newBalances)
        allBalances.forEachIndexed { index, item ->
            if(item.communityId.code == GlobalConstants.COMMUN_CODE){
                val tmp = allBalances[0]
                allBalances[0] = item
                allBalances[index] = tmp
                return@forEachIndexed
            }
        }
        balance = allBalances.filter { it.points > 0  || it.communityId == CommunityIdDomain(GlobalConstants.COMMUN_CODE) }
        currentBalanceRecord = initCurrentBalanceRecord()
        return Pair(balance.indexOf(currentBalanceRecord),currentBalanceRecord.points)
    }

    override fun getTransferCompletedInfo(): TransferCompletedInfo {
        val pointsName = if (currentCommunityId.code != GlobalConstants.COMMUN_CODE) {
            currentBalanceRecord.communityName ?: currentBalanceRecord.communityId.code
        } else {
            communName
        }

        return TransferCompletedInfo(
            date = Date(),
            amountTransfered = amount!!,
            amountRemain = currentBalanceRecord.points - calculateFee() - amount!!,
            userLogoUrl = sendToUser!!.avatarUrl,
            userName = sendToUser!!.username!!,
            pointsLogoUrl = currentBalanceRecord.communityLogoUrl,
            pointsName = pointsName,
            showFee = hasFee
        )
    }

    override fun getAmountAsString(): String? = amount?.toString()

    protected open fun initCurrentBalanceRecord() = calculateCurrentBalanceRecord()

    private fun calculateCurrentBalanceRecord() = balance.first { it.communityId == currentCommunityId }

    private fun calculateFee(): Double = if (hasFee) currentBalanceRecord.points / 1000 else 0.0
}