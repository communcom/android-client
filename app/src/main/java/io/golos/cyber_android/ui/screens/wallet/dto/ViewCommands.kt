package io.golos.cyber_android.ui.screens.wallet.dto

import io.golos.cyber_android.ui.shared.mvvm.view_commands.ViewCommand
import io.golos.domain.dto.WalletCommunityBalanceRecordDomain

class NavigateToWalletPoint(val selectedCommunityId: String, val balance: List<WalletCommunityBalanceRecordDomain>): ViewCommand

class ShowPointsDialog(val balance: List<WalletCommunityBalanceRecordDomain>): ViewCommand