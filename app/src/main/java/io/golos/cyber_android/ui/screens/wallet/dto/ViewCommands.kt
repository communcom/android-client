package io.golos.cyber_android.ui.screens.wallet.dto

import io.golos.cyber_android.ui.shared.mvvm.view_commands.ViewCommand
import io.golos.domain.dto.CommunityIdDomain
import io.golos.domain.dto.UserBriefDomain
import io.golos.domain.dto.WalletCommunityBalanceRecordDomain

class NavigateToWalletPoint(val selectedCommunityId: CommunityIdDomain, val balance: List<WalletCommunityBalanceRecordDomain>): ViewCommand

class NavigateToWalletSendPoints(
    val selectedCommunityId: CommunityIdDomain,
    val sendToUser: UserBriefDomain?,
    val balance: List<WalletCommunityBalanceRecordDomain>): ViewCommand

class NavigateToWalletConvertCommand(
    val selectedCommunityId: CommunityIdDomain,
    val balance: List<WalletCommunityBalanceRecordDomain>): ViewCommand

class ShowMyPointsDialog(val balance: List<WalletCommunityBalanceRecordDomain>): ViewCommand

class ShowSendPointsDialog(): ViewCommand

class ShowSettingsDialog(val emptyBalanceVisibility: Boolean): ViewCommand

class ShowFilterDialog(): ViewCommand