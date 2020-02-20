package io.golos.cyber_android.ui.screens.wallet.dto

import io.golos.cyber_android.ui.shared.mvvm.view_commands.ViewCommand
import io.golos.domain.dto.UserDomain
import io.golos.domain.dto.WalletCommunityBalanceRecordDomain

class NavigateToWalletPoint(val selectedCommunityId: String, val balance: List<WalletCommunityBalanceRecordDomain>): ViewCommand

class NavigateToWalletSendPoints(
    val selectedCommunityId: String,
    val sendToUser: UserDomain?,
    val balance: List<WalletCommunityBalanceRecordDomain>): ViewCommand

class NavigateToWalletConvertCommand(): ViewCommand

class ShowMyPointsDialog(val balance: List<WalletCommunityBalanceRecordDomain>): ViewCommand

class ShowSendPointsDialog(): ViewCommand