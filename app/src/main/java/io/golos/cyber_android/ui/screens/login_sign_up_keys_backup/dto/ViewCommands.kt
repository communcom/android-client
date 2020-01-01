package io.golos.cyber_android.ui.screens.login_sign_up_keys_backup.dto

import io.golos.cyber_android.ui.shared.mvvm.view_commands.ViewCommand
import io.golos.domain.dto.UserIdDomain

class NavigateToOnboardingCommand(val user: UserIdDomain): ViewCommand

class ShowBackupWarningDialogCommand: ViewCommand
