package io.golos.cyber_android.ui.screens.wallet_dialogs.transfer_completed

import java.util.*

data class TransferCompletedInfo(
    val date: Date,

    val amountTransfered: Double,
    val amountRemain: Double,

    val userLogoUrl: String?,
    val userName: String,

    val pointsLogoUrl: String?,
    val pointsName: String,

    val showFee: Boolean
)