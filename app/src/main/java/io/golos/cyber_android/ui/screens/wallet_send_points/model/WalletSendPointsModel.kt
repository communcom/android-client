package io.golos.cyber_android.ui.screens.wallet_send_points.model

import io.golos.cyber_android.ui.screens.wallet_point.dto.CarouselStartData
import io.golos.cyber_android.ui.screens.wallet_send_points.dto.AmountValidationResult
import io.golos.cyber_android.ui.shared.mvvm.model.ModelBase
import io.golos.domain.dto.UserDomain
import io.golos.domain.dto.WalletCommunityBalanceRecordDomain

interface WalletSendPointsModel : ModelBase {
    var sendToUser: UserDomain?

    val balance: List<WalletCommunityBalanceRecordDomain>

    val currentBalanceRecord: WalletCommunityBalanceRecordDomain

    val carouselItemsData: CarouselStartData

    val hasFee: Boolean

    fun updateAmount(amountAsString: String?): Boolean

    /**
     * @return Index of the community in the balance list
     */
    fun updateCurrentCommunity(communityId: String): Int?

    fun validateAmount(): AmountValidationResult

    suspend fun makeTransfer()
}