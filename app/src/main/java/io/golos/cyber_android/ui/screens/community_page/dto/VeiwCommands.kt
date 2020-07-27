package io.golos.cyber_android.ui.screens.community_page.dto

import io.golos.cyber_android.ui.shared.mvvm.view_commands.ViewCommand
import io.golos.domain.dto.CommunityIdDomain
import io.golos.domain.dto.WalletCommunityBalanceRecordDomain

class SwitchToLeadsTabCommand: ViewCommand

class NavigateToMembersCommand(val communityId: CommunityIdDomain): ViewCommand

class ShowCommunitySettings(val communityPage: CommunityPage?,val currentUserId:String):ViewCommand

class NavigateToFriendsCommand(val friends: List<CommunityFriend>): ViewCommand

class NavigateToWalletConvertCommand(
    val selectedCommunityId: CommunityIdDomain,
    val balance: List<WalletCommunityBalanceRecordDomain>): ViewCommand