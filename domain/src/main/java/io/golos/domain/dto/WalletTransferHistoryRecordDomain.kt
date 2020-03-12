package io.golos.domain.dto

import java.util.*

data class WalletTransferHistoryRecordDomain(
    val id: String,
    val transactionId: String,

    val senderId: UserIdDomain,
    val senderName: String?,
    val senderAvatarUrl: String?,

    val receiverId: UserIdDomain,
    val receiverName: String?,
    val receiverAvatarUrl: String?,

    val coinsQuantity: Double,
    val coinsSymbol: String,            // Name of the coin

    val communityName: String?,
    val communityAvatarUrl: String?,
    val communitySymbol: String,         // Name of the community coin

    val timeStamp: Date,                // The operation date&time

    val actionType: String,
    val transferType: String,
    val directionType: String,

    val amount: Double?,

    val holdType: String?
)