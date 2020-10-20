package io.golos.cyber_android.ui.screens.wallet_shared

import android.content.Context
import io.golos.cyber_android.R
import io.golos.domain.GlobalConstants
import io.golos.domain.dto.WalletCommunityBalanceRecordDomain
import io.golos.domain.dto.WalletTransferHistoryRecordDomain
import io.golos.utils.helpers.capitalize
import java.util.*

fun WalletCommunityBalanceRecordDomain.getDisplayName(context: Context): String =
    if(communityId.code != GlobalConstants.COMMUN_CODE) {
        communityName ?: communityId.code
    } else {
        context.getString(R.string.commun).capitalize(Locale.getDefault())
    }

fun WalletTransferHistoryRecordDomain.getDisplayName(context: Context): String =
    if(coinsSymbol != GlobalConstants.COMMUN_CODE) {
        communityName ?: communitySymbol
    } else {
        context.getString(R.string.commun).capitalize(Locale.getDefault())
    }
