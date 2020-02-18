package io.golos.cyber_android.ui.screens.wallet_send_points.model

import io.golos.cyber_android.ui.screens.wallet_point.dto.CarouselStartData
import io.golos.cyber_android.ui.screens.wallet_shared.carousel.CarouselListItem
import io.golos.cyber_android.ui.shared.mvvm.model.ModelBaseImpl
import io.golos.domain.dependency_injection.Clarification
import io.golos.domain.dto.UserDomain
import io.golos.domain.dto.WalletCommunityBalanceRecordDomain
import javax.inject.Inject
import javax.inject.Named

class WalletSendPointsModelImpl
@Inject
constructor(
    override var sendToUser: UserDomain?,
    @Named(Clarification.COMMUNITY_ID)
    private var currentCommunityId: String,
    @Named(Clarification.WALLET_POINT_BALANCE)
    override val balance: List<WalletCommunityBalanceRecordDomain>
) : ModelBaseImpl(), WalletSendPointsModel {

    override var currentBalanceRecord: WalletCommunityBalanceRecordDomain = calculateCurrentBalanceRecord()

    override val carouselItemsData: CarouselStartData = CarouselStartData(
        startIndex = balance.indexOfFirst { it.communityId == currentCommunityId },
        items = balance.map { CarouselListItem(id = it.communityId, iconUrl = it.communityLogoUrl) }
    )

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