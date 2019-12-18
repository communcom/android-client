package io.golos.cyber_android.ui.screens.login_sign_in_username.view

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.navigation.fragment.findNavController
import io.golos.cyber_android.R
import io.golos.cyber_android.application.App
import io.golos.cyber_android.databinding.FragmentSignInBinding
import io.golos.cyber_android.ui.common.extensions.setSoftDoneButtonListener
import io.golos.cyber_android.ui.common.mvvm.FragmentBaseMVVM
import io.golos.cyber_android.ui.common.mvvm.view_commands.BackCommand
import io.golos.cyber_android.ui.common.mvvm.view_commands.HideSoftKeyboardCommand
import io.golos.cyber_android.ui.common.mvvm.view_commands.ViewCommand
import io.golos.cyber_android.ui.screens.login_sign_in_username.di.SignInFragmentComponent
import io.golos.cyber_android.ui.screens.login_sign_in_username.dto.MoveToSignUpCommand
import io.golos.cyber_android.ui.screens.login_sign_in_username.dto.SetPasswordFocusCommand
import io.golos.cyber_android.ui.screens.login_sign_in_username.dto.SetUserNameFocusCommand
import io.golos.cyber_android.ui.screens.login_sign_in_username.view_model.SignInViewModel
import kotlinx.android.synthetic.main.fragment_sign_in.*

class SignInFragment : FragmentBaseMVVM<FragmentSignInBinding, SignInViewModel>() {
    companion object {
        fun newInstance() = SignInFragment()
    }

    override fun provideViewModelType(): Class<SignInViewModel> = SignInViewModel::class.java

    override fun layoutResId(): Int = R.layout.fragment_sign_in

    override fun inject() = App.injections.get<SignInFragmentComponent>().inject(this)

    override fun releaseInjection() {
        App.injections.release<SignInFragmentComponent>()
    }

    override fun linkViewModel(binding: FragmentSignInBinding, viewModel: SignInViewModel) {
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
        }
    }
}