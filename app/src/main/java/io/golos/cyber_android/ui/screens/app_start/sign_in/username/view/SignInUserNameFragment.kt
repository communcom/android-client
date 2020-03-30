package io.golos.cyber_android.ui.screens.app_start.sign_in.username.view

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.text.InputFilter
import android.view.View
import androidx.navigation.fragment.findNavController
import io.golos.cyber_android.R
import io.golos.cyber_android.application.App
import io.golos.cyber_android.databinding.FragmentSignInUserNameBinding
import io.golos.cyber_android.ui.shared.extensions.setSoftDoneButtonListener
import io.golos.cyber_android.ui.shared.mvvm.FragmentBaseMVVM
import io.golos.cyber_android.ui.shared.mvvm.view_commands.NavigateBackwardCommand
import io.golos.cyber_android.ui.shared.mvvm.view_commands.HideSoftKeyboardCommand
import io.golos.cyber_android.ui.shared.mvvm.view_commands.NavigateForwardCommand
import io.golos.cyber_android.ui.shared.mvvm.view_commands.ViewCommand
import io.golos.cyber_android.ui.screens.app_start.sign_in.username.di.SignInUserNameFragmentComponent
import io.golos.cyber_android.ui.screens.app_start.sign_in.username.dto.NavigateToQrCodeCommand
import io.golos.cyber_android.ui.screens.app_start.sign_in.username.dto.NavigateToSignUpCommand
import io.golos.cyber_android.ui.screens.app_start.sign_in.username.dto.SetPasswordFocusCommand
import io.golos.cyber_android.ui.screens.app_start.sign_in.username.dto.SetUserNameFocusCommand
import io.golos.cyber_android.ui.screens.app_start.sign_in.username.view_model.SignInUserNameViewModel
import io.golos.cyber_android.ui.screens.app_start.sign_up.activity.SignUpActivity
import io.golos.cyber_android.ui.shared.text.AllLowersInputFilter
import io.golos.cyber_android.ui.shared.text.ExcludeCharactersFilter
import kotlinx.android.synthetic.main.fragment_sign_in_user_name.*
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.OnPermissionDenied
import permissions.dispatcher.RuntimePermissions

@RuntimePermissions
class SignInUserNameFragment : FragmentBaseMVVM<FragmentSignInUserNameBinding, SignInUserNameViewModel>() {
    override val isBackHandlerEnabled: Boolean = true

    override fun provideViewModelType(): Class<SignInUserNameViewModel> = SignInUserNameViewModel::class.java

    override fun layoutResId(): Int = R.layout.fragment_sign_in_user_name

    override fun inject(key: String) = App.injections.get<SignInUserNameFragmentComponent>(key).inject(this)

    override fun releaseInjection(key: String) = App.injections.release<SignInUserNameFragmentComponent>(key)

    override fun linkViewModel(binding: FragmentSignInUserNameBinding, viewModel: SignInUserNameViewModel) {
        binding.viewModel = viewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        header.setOnBackButtonClickListener { viewModel.onBackButtonClick() }

        // Done action on a soft keyboard
        login.filters = arrayOf(
            InputFilter.LengthFilter(viewModel.maxUserNameLen),
            AllLowersInputFilter(),
            ExcludeCharactersFilter(' ', ignoreCase = false)
        )
        password.setSoftDoneButtonListener { viewModel.onSignInClick() }
    }

    override fun processViewCommand(command: ViewCommand) {
        when(command) {
            is NavigateBackwardCommand -> requireActivity().onBackPressed()
            is HideSoftKeyboardCommand -> uiHelper.setSoftKeyboardVisibility(login, false)
            is SetUserNameFocusCommand -> login.requestFocus()
            is SetPasswordFocusCommand -> password.requestFocus()
            is NavigateToSignUpCommand -> moveToSignUp()
            is NavigateToQrCodeCommand -> moveToQrCodeWithPermissionCheck()
            is NavigateForwardCommand -> findNavController().navigate(R.id.action_signInUserNameFragment_to_signInPinCodeFragment)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        onRequestPermissionsResult(requestCode, grantResults)
    }

    override fun onResume() {
        super.onResume()
        login.post {viewModel.processResumedActions()}      // Don't move it from "post" call - it doesn't work in Android Q
    }

    @NeedsPermission(Manifest.permission.CAMERA)
    fun moveToQrCode() {
        findNavController().navigate(R.id.action_signInUserNameFragment_to_signInQrCodeFragment)
    }

    @OnPermissionDenied(Manifest.permission.CAMERA)
    internal fun onCameraPermissionsDenied() = viewModel.onQrCodeCameraPermissionsDenied()

    private fun moveToSignUp() {
        val intent = Intent(requireContext(), SignUpActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }
}