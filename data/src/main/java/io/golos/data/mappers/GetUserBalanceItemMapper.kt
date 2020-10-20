package io.golos.data.mappers

import io.golos.commun4j.services.model.GetUserBalanceItem
import io.golos.domain.dto.CommunityIdDomain
import io.golos.domain.dto.WalletCommunityBalanceRecordDomain

fun GetUserBalanceItem.mapToWalletCommunityBalanceRecordDomain() =
    WalletCommunityBalanceRecordDomain(
        points = balance,
        frozenPoints = frozen,

        communityLogoUrl = logo,
        communityName = name,
        communityId = CommunityIdDomain(symbol.value),

        /**
         * Price of all points in Communs
         */
        communs = price,
        transferFee = transferFee
)