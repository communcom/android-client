package io.golos.data.mappers

import io.golos.commun4j.services.model.GetUserBalanceItem
import io.golos.domain.dto.WalletCommunityBalanceRecordDomain

fun GetUserBalanceItem.mapToWalletCommunityBalanceRecordDomain() =
    WalletCommunityBalanceRecordDomain(
        points = balance,
        frozenPoints = frozen,

        communityLogoUrl = logo,
        communityName = name,
        communityId = symbol.value,

        communs = price,
        transferFee = transferFee
)