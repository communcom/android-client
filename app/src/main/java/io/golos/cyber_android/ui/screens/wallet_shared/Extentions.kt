package io.golos.cyber_android.ui.screens.wallet_shared

import android.content.Context
import io.golos.cyber_android.R
import io.golos.domain.GlobalConstants
import io.golos.domain.dto.WalletCommunityBalanceRecordDomain
import io.golos.utils.capitalize
import java.util.*

fun WalletCommunityBalanceRecordDomain.getDisplayName(context: Context) {
    if(communityId != GlobalConstants.COMMUN_CODE) {
        communityName ?: communityId
    } else {
        context.getString(R.string.commun).capitalize(Locale.getDefault())
    }
}