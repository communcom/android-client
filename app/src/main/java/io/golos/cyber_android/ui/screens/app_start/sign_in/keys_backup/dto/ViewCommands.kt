package io.golos.cyber_android.ui.screens.app_start.sign_in.keys_backup.dto

import io.golos.cyber_android.ui.shared.mvvm.view_commands.ViewCommand
import java.io.File

class ShowBackupWarningDialogCommand: ViewCommand

class ShowSaveDialogCommand() : ViewCommand

class StartExportToGoogleDriveCommand(val fileToExport: File) : ViewCommand
