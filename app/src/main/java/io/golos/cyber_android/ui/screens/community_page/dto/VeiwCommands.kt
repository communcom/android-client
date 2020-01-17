package io.golos.cyber_android.ui.screens.community_page.dto

import io.golos.cyber_android.ui.shared.mvvm.view_commands.ViewCommand

class SwitchToLeadsTabCommand: ViewCommand

class NavigateToMembersCommand(val communityId: String): ViewCommand

class NavigateToFriendsCommand(val friends: List<CommunityFriend>): ViewCommand