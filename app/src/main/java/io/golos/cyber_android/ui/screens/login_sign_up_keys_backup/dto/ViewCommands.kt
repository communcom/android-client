package io.golos.cyber_android.ui.screens.login_sign_up_keys_backup.dto

import io.golos.cyber_android.ui.shared.mvvm.view_commands.ViewCommand
import io.golos.domain.dto.UserIdDomain
import io.golos.domain.dto.UserKey

class ShowBackupWarningDialogCommand: ViewCommand

class StartExportingCommand(
    val userName: String,
    val userId: UserIdDomain,
    val keys: List<UserKey>
) : ViewCommand