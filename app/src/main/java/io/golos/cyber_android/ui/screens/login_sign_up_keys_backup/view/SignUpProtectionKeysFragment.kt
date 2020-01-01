package io.golos.cyber_android.ui.screens.login_sign_up_keys_backup.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import io.golos.cyber_android.R
import io.golos.cyber_android.application.App
import io.golos.cyber_android.databinding.FragmentSignUpProtectionKeysBinding
import io.golos.cyber_android.ui.shared.Tags
import io.golos.cyber_android.ui.shared.extensions.safeNavigate
import io.golos.cyber_android.ui.shared.keys_to_pdf.PdfKeysExporter
import io.golos.cyber_android.ui.shared.keys_to_pdf.StartExportingCommand
import io.golos.cyber_android.ui.shared.mvvm.FragmentBaseMVVM
import io.golos.cyber_android.ui.shared.mvvm.view_commands.ViewCommand
import io.golos.cyber_android.ui.dialogs.KeysBackupWarningDialog
import io.golos.cyber_android.ui.screens.login_sign_up.fragments.onboardingImage.OnboardingUserImageFragment
import io.golos.cyber_android.ui.screens.login_sign_up_keys_backup.di.SignUpProtectionKeysFragmentComponent
import io.golos.cyber_android.ui.screens.login_sign_up_keys_backup.dto.NavigateToOnboardingCommand
import io.golos.cyber_android.ui.screens.login_sign_up_keys_backup.dto.ShowBackupWarningDialogCommand
import io.golos.cyber_android.ui.screens.login_sign_up_keys_backup.view_model.SignUpProtectionKeysViewModel
import io.golos.domain.dto.UserIdDomain
import kotlinx.android.synthetic.main.fragment_sign_up_protection_keys.*


class SignUpProtectionKeysFragment : FragmentBaseMVVM<FragmentSignUpProtectionKeysBinding, SignUpProtectionKeysViewModel>() {
    private val keysExporter by lazy { PdfKeysExporter(this) }

    override fun provideViewModelType(): Class<SignUpProtectionKeysViewModel> = SignUpProtectionKeysViewModel::class.java

    override fun layoutResId(): Int = R.layout.fragment_sign_up_protection_keys

    override fun inject() = App.injections.get<SignUpProtectionKeysFragmentComponent>().inject(this)

    override fun releaseInjection() {
        App.injections.release<SignUpProtectionKeysFragmentComponent>()
    }

    override fun linkViewModel(binding: FragmentSignUpProtectionKeysBinding, viewModel: SignUpProtectionKeysViewModel) {
        binding.viewModel = viewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        backup.setOnClickListener { keysExporter.startExport() }
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
            is NavigateToOnboardingCommand -> navigateToOnboarding(command.user)
            is StartExportingCommand -> keysExporter.processDataToExport(command.userName, command.userId, command.keys)
            is ShowBackupWarningDialogCommand -> showBackupWarningCommand()
            else -> throw UnsupportedOperationException("This command is not supported")
        }
    }

    private fun navigateToOnboarding(user: UserIdDomain) {
        findNavController().safeNavigate(
            R.id.signUpProtectionKeysFragment,
            R.id.action_signUpProtectionKeysFragment_to_onboardingUserImageFragment,
            Bundle().apply { putParcelable(Tags.ARGS, OnboardingUserImageFragment.Args(user.userId) )}
        )
    }

    private fun showBackupWarningCommand() {
        KeysBackupWarningDialog.newInstance(this@SignUpProtectionKeysFragment).show(requireFragmentManager(), "menu")
    }
}
