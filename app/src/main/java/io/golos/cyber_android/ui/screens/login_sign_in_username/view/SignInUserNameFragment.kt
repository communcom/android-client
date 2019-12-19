package io.golos.cyber_android.ui.screens.login_sign_in_username.view

import android.Manifest
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.navigation.fragment.findNavController
import io.golos.cyber_android.R
import io.golos.cyber_android.application.App
import io.golos.cyber_android.databinding.FragmentSignInUserNameBinding
import io.golos.cyber_android.ui.common.extensions.setSoftDoneButtonListener
import io.golos.cyber_android.ui.common.mvvm.FragmentBaseMVVM
import io.golos.cyber_android.ui.common.mvvm.view_commands.BackCommand
import io.golos.cyber_android.ui.common.mvvm.view_commands.HideSoftKeyboardCommand
import io.golos.cyber_android.ui.common.mvvm.view_commands.ViewCommand
import io.golos.cyber_android.ui.screens.login_sign_in_username.di.SignInUserNameFragmentComponent
import io.golos.cyber_android.ui.screens.login_sign_in_username.dto.MoveToQrCodeCommand
import io.golos.cyber_android.ui.screens.login_sign_in_username.dto.MoveToSignUpCommand
import io.golos.cyber_android.ui.screens.login_sign_in_username.dto.SetPasswordFocusCommand
import io.golos.cyber_android.ui.screens.login_sign_in_username.dto.SetUserNameFocusCommand
import io.golos.cyber_android.ui.screens.login_sign_in_username.view_model.SignInUserNameViewModel
import kotlinx.android.synthetic.main.fragment_sign_in_user_name.*
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.OnPermissionDenied
import permissions.dispatcher.RuntimePermissions

@RuntimePermissions
class SignInUserNameFragment : FragmentBaseMVVM<FragmentSignInUserNameBinding, SignInUserNameViewModel>() {
    companion object {
        fun newInstance() = SignInUserNameFragment()
    }

    override fun provideViewModelType(): Class<SignInUserNameViewModel> = SignInUserNameViewModel::class.java

    override fun layoutResId(): Int = R.layout.fragment_sign_in_user_name

    override fun inject() = App.injections.get<SignInUserNameFragmentComponent>().inject(this)

    override fun releaseInjection() {
        App.injections.release<SignInUserNameFragmentComponent>()
    }

    override fun linkViewModel(binding: FragmentSignInUserNameBinding, viewModel: SignInUserNameViewModel) {
        binding.viewModel = viewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Done action on a soft keyboard
        password.setSoftDoneButtonListener { viewModel.onSignInClick() }
    }

    override fun processViewCommand(command: ViewCommand) {
        when(command) {
            is BackCommand -> requireActivity().onBackPressed()
            is HideSoftKeyboardCommand -> uiHelper.setSoftKeyboardVisibility(login, false)
            is SetUserNameFocusCommand -> login.requestFocus()
            is SetPasswordFocusCommand -> password.requestFocus()
            is MoveToSignUpCommand -> findNavController().navigate(R.id.action_signInFragment_to_signUpPhoneFragment)
            is MoveToQrCodeCommand -> moveToQrCodeWithPermissionCheck()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        onRequestPermissionsResult(requestCode, grantResults)
    }

    override fun onResume() {
        super.onResume()
        login.post {viewModel.tryToGetPassFromClipboard()}      // Don't move it from "post" call - it doesn't work in Android Q
    }

    @NeedsPermission(Manifest.permission.CAMERA)
    fun moveToQrCode() {
        findNavController().navigate(R.id.action_signInFragment_to_signInQrCodeFragment)
    }

    @OnPermissionDenied(Manifest.permission.CAMERA)
    internal fun onCameraPermissionsDenied() = viewModel.onQrCodeCameraPermissionsDenied()
}