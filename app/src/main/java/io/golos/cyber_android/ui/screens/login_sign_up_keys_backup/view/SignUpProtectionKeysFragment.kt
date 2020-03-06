package io.golos.cyber_android.ui.screens.login_sign_up_keys_backup.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import io.golos.cyber_android.R
import io.golos.cyber_android.application.App
import io.golos.cyber_android.databinding.FragmentSignUpProtectionKeysBinding
import io.golos.cyber_android.ui.dialogs.KeysBackupWarningDialog
import io.golos.cyber_android.ui.dialogs.SavePdfActionDialog
import io.golos.cyber_android.ui.screens.login_sign_up_keys_backup.di.SignUpProtectionKeysFragmentComponent
import io.golos.cyber_android.ui.screens.login_sign_up_keys_backup.dto.ShowBackupWarningDialogCommand
import io.golos.cyber_android.ui.screens.login_sign_up_keys_backup.dto.ShowSaveDialogCommand
import io.golos.cyber_android.ui.screens.login_sign_up_keys_backup.dto.StartExportToGoogleDriveCommand
import io.golos.cyber_android.ui.screens.login_sign_up_keys_backup.view_model.SignUpProtectionKeysViewModel
import io.golos.cyber_android.ui.screens.main_activity.MainActivity
import io.golos.cyber_android.ui.shared.mvvm.FragmentBaseMVVM
import io.golos.cyber_android.ui.shared.mvvm.view_commands.NavigateToMainScreenCommand
import io.golos.cyber_android.ui.shared.mvvm.view_commands.ViewCommand
import kotlinx.android.synthetic.main.fragment_sign_up_protection_keys.*


class SignUpProtectionKeysFragment : FragmentBaseMVVM<FragmentSignUpProtectionKeysBinding, SignUpProtectionKeysViewModel>() {
    private val keysToLocalDriveExporter by lazy { KeysToLocalDriveExporter(this) }
    private val keysToGoogleDriveExporter by lazy { KeysToGoogleDriveExporter(this) }

    override fun provideViewModelType(): Class<SignUpProtectionKeysViewModel> = SignUpProtectionKeysViewModel::class.java

    override fun layoutResId(): Int = R.layout.fragment_sign_up_protection_keys

    override fun inject(key: String) = App.injections.get<SignUpProtectionKeysFragmentComponent>(key).inject(this)

    override fun releaseInjection(key: String) = App.injections.release<SignUpProtectionKeysFragmentComponent>(key)

    override fun linkViewModel(binding: FragmentSignUpProtectionKeysBinding, viewModel: SignUpProtectionKeysViewModel) {
        binding.viewModel = viewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        explanationLabel.text = this.resources.getText(R.string.need_password)

        backup.setOnClickListener { viewModel.onBackupClick() }
        keysToLocalDriveExporter.setOnExportPathSelectedListener { viewModel.onExportPathSelected(it) }
        keysToLocalDriveExporter.setOnExportErrorListener { uiHelper.showMessage(R.string.export_general_error) }

        keysToGoogleDriveExporter.setOnExportStateListener {
            when(it) {
                KeysToGoogleDriveExporter.ExportState.STARTED -> { viewModel.onExportToGoogleDriveStarted() }
                KeysToGoogleDriveExporter.ExportState.SUCCESS -> { viewModel.onExportToGoogleDriveSuccess() }
                KeysToGoogleDriveExporter.ExportState.FAIL -> { viewModel.onExportToGoogleDriveFail() }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.d("UPLOAD", "onActivityResult(requestCode: $requestCode; resultCode: $resultCode)")
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == KeysBackupWarningDialog.REQUEST) {
            when (resultCode) {
                KeysBackupWarningDialog.RESULT_CONTINUE -> viewModel.onWarningContinueClick()
            }
        } else {
            keysToGoogleDriveExporter.processSignInResult(requestCode, resultCode, data)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        keysToLocalDriveExporter.processRequestPermissionsResult(requestCode, grantResults)
    }

    override fun processViewCommand(command: ViewCommand) {
        when(command) {
            is NavigateToMainScreenCommand -> navigateToMainScreen()
            is ShowBackupWarningDialogCommand -> showBackupWarningCommand()
            is ShowSaveDialogCommand -> showSaveDialog()
            is StartExportToGoogleDriveCommand -> keysToGoogleDriveExporter.startExport(command.fileToExport)
            else -> throw UnsupportedOperationException("This command is not supported")
        }
    }

    private fun showBackupWarningCommand() {
        KeysBackupWarningDialog.newInstance(this@SignUpProtectionKeysFragment).show(requireFragmentManager(), "menu")
    }

    private fun navigateToMainScreen() {
        if (!requireActivity().isFinishing) {
            requireActivity().finish()
            startActivity(Intent(requireContext(), MainActivity::class.java))
        }
    }

    private fun showSaveDialog() {
        SavePdfActionDialog.show(this) {
            when(it) {
                SavePdfActionDialog.Result.Device -> keysToLocalDriveExporter.startExport()
                SavePdfActionDialog.Result.GoogleDrive -> { viewModel.onExportToGoogleDriveSelected() }
            }
        }
    }
}
