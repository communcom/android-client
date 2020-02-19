package io.golos.cyber_android.ui.screens.wallet_send_points.dto

import io.golos.cyber_android.ui.shared.mvvm.view_commands.ViewCommand
import io.golos.domain.dto.WalletCommunityBalanceRecordDomain

class ShowSelectUserDialogCommand: ViewCommand

class ShowSelectCommunityDialogCommand(val balance: List<WalletCommunityBalanceRecordDomain>): ViewCommand

class UpdateCarouselPositionCommand(val position: Int): ViewCommand

class HideKeyboardCommand(): ViewCommand