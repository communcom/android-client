package io.golos.cyber_android.ui.screens.login.signup.keys_backup.view_commands

import io.golos.cyber_android.ui.common.mvvm.ViewCommand
import io.golos.domain.entities.UserKey

/**
 * Command for starting export to Pdf
 */
class StartExportingCommand(
    val pathToSave: String,
    val userName: String,
    val keys: List<UserKey>
) : ViewCommand