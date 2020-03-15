package io.golos.cyber_android.ui.screens.wallet.view.my_points

import io.golos.domain.dto.CommunityIdDomain

interface WalletMyPointsListItemEventsProcessor {
    fun onMyPointItemClick(communityId: CommunityIdDomain)
}
