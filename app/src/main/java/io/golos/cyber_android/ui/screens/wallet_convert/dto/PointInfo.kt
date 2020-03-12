package io.golos.cyber_android.ui.screens.wallet_convert.dto

data class PointInfo(
    val sellerName: String,

    val buyerName: String,
    val buyerLogoUrl: String?,
    val buyerBalance: Double,

    val canSelectPoint: Boolean,

    val rateInfo: String
)