package io.golos.cyber_android.ui.screens.wallet_convert.model

import io.golos.cyber_android.ui.screens.wallet_convert.model.amount_calculator.AmountCalculatorBrief
import io.golos.cyber_android.ui.screens.wallet_point.dto.CarouselStartData
import io.golos.cyber_android.ui.shared.mvvm.model.ModelBase
import io.golos.domain.dto.WalletCommunityBalanceRecordDomain

interface WalletConvertModel : ModelBase {
    val amountCalculator: AmountCalculatorBrief

    val balance: List<WalletCommunityBalanceRecordDomain>

    val currentBalanceRecord: WalletCommunityBalanceRecordDomain

    val carouselItemsData: CarouselStartData

    val isInSellPointMode: Boolean

    fun getSellerRecord(): WalletCommunityBalanceRecordDomain

    fun getBuyerRecord(): WalletCommunityBalanceRecordDomain

    /**
     * @return Index of the community in the balance list
     */
    fun updateCurrentCommunity(communityId: String): Int?

    fun swipeSellMode()
}