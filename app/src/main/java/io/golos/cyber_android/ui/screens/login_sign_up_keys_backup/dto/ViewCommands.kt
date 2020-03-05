package io.golos.cyber_android.ui.screens.login_sign_up_keys_backup.dto

import io.golos.cyber_android.ui.shared.mvvm.view_commands.ViewCommand

class ShowBackupWarningDialogCommand: ViewCommand

class StartExportingKeyCommand(val exportData: PdfPageExportData) : ViewCommand

class ShowSaveDialogCommand() : ViewCommand