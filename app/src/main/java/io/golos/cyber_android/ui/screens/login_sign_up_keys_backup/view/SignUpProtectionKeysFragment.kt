package io.golos.cyber_android.ui.screens.login_sign_up_keys_backup.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import io.golos.cyber_android.R
import io.golos.cyber_android.application.App
import io.golos.cyber_android.databinding.FragmentSignUpProtectionKeysBinding
import io.golos.cyber_android.ui.dialogs.KeysBackupWarningDialog
import io.golos.cyber_android.ui.dialogs.SavePdfActionDialog
import io.golos.cyber_android.ui.screens.login_sign_up_keys_backup.di.SignUpProtectionKeysFragmentComponent
import io.golos.cyber_android.ui.screens.login_sign_up_keys_backup.dto.ShowBackupWarningDialogCommand
import io.golos.cyber_android.ui.screens.login_sign_up_keys_backup.dto.ShowSaveDialogCommand
import io.golos.cyber_android.ui.screens.login_sign_up_keys_backup.dto.StartExportingKeyCommand
import io.golos.cyber_android.ui.screens.login_sign_up_keys_backup.view_model.SignUpProtectionKeysViewModel
import io.golos.cyber_android.ui.screens.main_activity.MainActivity
import io.golos.cyber_android.ui.screens.login_sign_up_keys_backup.pdf_export.PdfKeysExporter
import io.golos.cyber_android.ui.shared.mvvm.FragmentBaseMVVM
import io.golos.cyber_android.ui.shared.mvvm.view_commands.NavigateToMainScreenCommand
import io.golos.cyber_android.ui.shared.mvvm.view_commands.ViewCommand
import kotlinx.android.synthetic.main.fragment_sign_up_protection_keys.*


class SignUpProtectionKeysFragment : FragmentBaseMVVM<FragmentSignUpProtectionKeysBinding, SignUpProtectionKeysViewModel>() {
    private val keysExporter by lazy { PdfKeysExporter(this) }

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

        backup.setOnClickListener { viewModel.onBackupClick() /*keysExporter.startExport()*/ }
        keysExporter.setOnExportCompletedListener { viewModel.onBackupCompleted() }
        keysExporter.setOnExportPathSelectedListener { viewModel.onExportPathSelected() }
        keysExporter.setOnExportErrorListener { uiHelper.showMessage(R.string.export_general_error) }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == KeysBackupWarningDialog.REQUEST) {
            when (resultCode) {
                KeysBackupWarningDialog.RESULT_CONTINUE -> viewModel.onWarningContinueClick()
            }
        } else {
            keysExporter.processViewPdfResult(requestCode)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        keysExporter.processRequestPermissionsResult(requestCode, grantResults)
    }

    override fun processViewCommand(command: ViewCommand) {
        when(command) {
            is NavigateToMainScreenCommand -> navigateToMainScreen()
            is StartExportingKeyCommand -> keysExporter.processDataToExport(requireContext(), command.exportData)
            is ShowBackupWarningDialogCommand -> showBackupWarningCommand()
            is ShowSaveDialogCommand -> showSaveDialog()
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
                SavePdfActionDialog.Result.Device -> keysExporter.startExport()
                SavePdfActionDialog.Result.GoogleDrive -> {}
            }
        }
    }
}
