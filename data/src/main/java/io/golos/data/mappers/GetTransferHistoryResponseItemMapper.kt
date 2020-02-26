package io.golos.data.mappers

import io.golos.commun4j.services.model.GetTransferHistoryResponseItem
import io.golos.domain.dto.UserIdDomain
import io.golos.domain.dto.WalletTransferHistoryRecordDomain

fun GetTransferHistoryResponseItem.mapToWalletTransferHistoryRecordDomain() =
    WalletTransferHistoryRecordDomain(
        id = id,
        transactionId = trxId,

        senderId = UserIdDomain(sender.userId.name),
        senderName = sender.username,
        senderAvatarUrl = sender.avatarUrl,

        receiverId = UserIdDomain(receiver.userId.name),
        receiverName = receiver.username,
        receiverAvatarUrl = receiver.avatarUrl,

        coinsQuantity = quantity,
        coinsSymbol = symbol.value,

        communityName = point?.name,
        communityAvatarUrl = point?.logo,
        communitySymbol = point?.symbol?.value ?: symbol.value,

        timeStamp = timestamp,

        actionType = meta.actionType,
        transferType = meta.transferType.toString(),
        directionType = meta.direction.toString(),

        amount = meta.exchangeAmount,
        holdType = meta.holdType
)