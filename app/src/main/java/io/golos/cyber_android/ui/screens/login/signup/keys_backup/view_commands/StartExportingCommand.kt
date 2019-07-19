package io.golos.cyber_android.ui.screens.login.signup.keys_backup.view_commands

import io.golos.cyber_android.ui.common.mvvm.view_commands.ViewCommand
import io.golos.domain.entities.UserKey

/**
 * Command for starting export to Pdf
 */
class StartExportingCommand(
    val pathToSave: String,
    val userName: String,
    val userId: String,
    val keys: List<UserKey>
) : ViewCommand