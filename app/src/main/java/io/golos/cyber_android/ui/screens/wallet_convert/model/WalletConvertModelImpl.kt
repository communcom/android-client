package io.golos.cyber_android.ui.screens.wallet_convert.model

import io.golos.cyber_android.ui.screens.wallet_point.dto.CarouselStartData
import io.golos.cyber_android.ui.screens.wallet_shared.carousel.CarouselListItem
import io.golos.cyber_android.ui.shared.mvvm.model.ModelBaseImpl
import io.golos.domain.GlobalConstants
import io.golos.domain.dependency_injection.Clarification
import io.golos.domain.dto.WalletCommunityBalanceRecordDomain
import javax.inject.Inject
import javax.inject.Named

class WalletConvertModelImpl
@Inject
constructor(
    @Named(Clarification.COMMUNITY_ID)
    private var currentCommunityId: String,
    @Named(Clarification.WALLET_POINT_BALANCE)
    override var balance: List<WalletCommunityBalanceRecordDomain>
): WalletConvertModel, ModelBaseImpl() {

    private var sellAmount: Double? = null
    private var buyAmount: Double? = null

    @Suppress("JoinDeclarationAndAssignment")
    private val communBalanceRecord: WalletCommunityBalanceRecordDomain

    override var currentBalanceRecord = calculateCurrentBalanceRecord()

    override val carouselItemsData: CarouselStartData

    override var isInSellPointMode: Boolean = true

    init {
        communBalanceRecord = balance.first { it.communityId == GlobalConstants.COMMUN_CODE }

        // Remove Commun record from the balance
        balance = balance.filter { it.communityId != GlobalConstants.COMMUN_CODE }

        carouselItemsData = CarouselStartData(
            startIndex = balance.indexOfFirst { it.communityId == currentCommunityId },
            items = balance.map { CarouselListItem(id = it.communityId, iconUrl = it.communityLogoUrl) }
        )
    }

    override fun getSellerRecord(): WalletCommunityBalanceRecordDomain =
        if(isInSellPointMode) currentBalanceRecord else communBalanceRecord

    override fun getBuyerRecord(): WalletCommunityBalanceRecordDomain =
        if(isInSellPointMode) communBalanceRecord else currentBalanceRecord

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

    override fun swipeSellMode() {
        isInSellPointMode = !isInSellPointMode
    }

    private fun calculateCurrentBalanceRecord() = balance.first { it.communityId == currentCommunityId }
}