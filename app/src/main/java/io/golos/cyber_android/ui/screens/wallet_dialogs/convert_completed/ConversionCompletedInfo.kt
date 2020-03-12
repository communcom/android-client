package io.golos.cyber_android.ui.screens.wallet_dialogs.convert_completed

import java.util.*

data class ConversionCompletedInfo(
    val date: Date,

    val coins: Double,

    val sellerAvatarUrl: String?,
    val sellerName: String,
    val sellerPointsTotal: Double,

    val buyerAvatarUrl: String?,
    val buyerName: String,
    val buyerPointsTotal: Double
)