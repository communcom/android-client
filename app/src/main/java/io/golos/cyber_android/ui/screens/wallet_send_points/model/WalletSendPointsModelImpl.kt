package io.golos.cyber_android.ui.screens.wallet_send_points.model

import android.content.Context
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.screens.wallet_point.dto.CarouselStartData
import io.golos.cyber_android.ui.screens.wallet_shared.carousel.CarouselListItem
import io.golos.cyber_android.ui.shared.mvvm.model.ModelBaseImpl
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
    override var balance: List<WalletCommunityBalanceRecordDomain>
) : ModelBaseImpl(), WalletSendPointsModel {

    private var amount: Double? = null

    init {
        // Move Commun community to the first
        balance =
            mutableListOf(
                balance.first { it.communityId == GlobalConstants.COMMUN_CODE }
                    .copy(communityName = appContext.getString(R.string.commun).capitalize(Locale.getDefault()))
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

    private fun calculateCurrentBalanceRecord() = balance.first { it.communityId == currentCommunityId }
}