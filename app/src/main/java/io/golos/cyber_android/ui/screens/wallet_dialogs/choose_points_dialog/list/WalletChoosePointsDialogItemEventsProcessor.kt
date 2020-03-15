package io.golos.cyber_android.ui.screens.wallet_dialogs.choose_points_dialog.list

import io.golos.domain.dto.CommunityIdDomain

interface WalletChoosePointsDialogItemEventsProcessor {
    fun onItemClick(communityId: CommunityIdDomain)
}