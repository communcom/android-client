package io.golos.cyber_android.ui.common.keys_to_pdf

import io.golos.cyber_android.ui.common.mvvm.view_commands.ViewCommand
import io.golos.domain.dto.UserKey

/**
 * Command for starting export to Pdf
 */
class StartExportingCommand(
    val userName: String,
    val userId: String,
    val keys: List<UserKey>
) : ViewCommand