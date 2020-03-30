package io.golos.cyber_android.ui.screens.app_start.sign_up.username.view

import android.os.Bundle
import android.text.InputFilter
import android.view.View
import androidx.navigation.fragment.findNavController
import io.golos.cyber_android.R
import io.golos.cyber_android.application.App
import io.golos.cyber_android.databinding.FragmentSignUpNameBinding
import io.golos.cyber_android.ui.dialogs.SimpleTextBottomSheetDialog
import io.golos.cyber_android.ui.screens.app_start.sign_up.username.di.SignUpNameFragmentComponent
import io.golos.cyber_android.ui.screens.app_start.sign_up.username.dto.NavigateToGetPasswordCommand
import io.golos.cyber_android.ui.screens.app_start.sign_up.username.view_model.SignUpNameViewModel
import io.golos.cyber_android.ui.screens.app_start.sign_up.phone.dto.NavigateToSelectSignUpMethodCommand
import io.golos.cyber_android.ui.shared.extensions.setOnDrawableEndClickListener
import io.golos.cyber_android.ui.shared.mvvm.FragmentBaseMVVM
import io.golos.cyber_android.ui.shared.mvvm.view_commands.ViewCommand
import io.golos.cyber_android.ui.shared.text.AllLowersInputFilter
import kotlinx.android.synthetic.main.fragment_sign_up_name.*

class SignUpNameFragment : FragmentBaseMVVM<FragmentSignUpNameBinding, SignUpNameViewModel>() {
    override val isBackHandlerEnabled: Boolean = true

    override fun provideViewModelType(): Class<SignUpNameViewModel> = SignUpNameViewModel::class.java

    override fun layoutResId(): Int = R.layout.fragment_sign_up_name

    override fun inject(key: String) = App.injections.get<SignUpNameFragmentComponent>(key).inject(this)

    override fun releaseInjection(key: String) = App.injections.release<SignUpNameFragmentComponent>(key)

    override fun linkViewModel(binding: FragmentSignUpNameBinding, viewModel: SignUpNameViewModel) {
        binding.viewModel = viewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        username.filters = arrayOf(
            InputFilter.LengthFilter(viewModel.maxUserNameLen),
            AllLowersInputFilter()
        )

        username.setOnDrawableEndClickListener {
            uiHelper.setSoftKeyboardVisibility(username, false)
            showExplanationDialog()
        }

        username?.post { uiHelper.setSoftKeyboardVisibility(username, true) }

        next.setOnClickListener { viewModel.onNextClick() }

        viewModel.validationResult.observe({viewLifecycleOwner.lifecycle}) {
            errorText.visibility = if(it.isValid) View.INVISIBLE else View.VISIBLE
            errorText.text = it.erroText
        }
    }

    override fun processViewCommand(command: ViewCommand) {
        when(command) {
            is NavigateToSelectSignUpMethodCommand -> moveToSelectMethod()
            is NavigateToGetPasswordCommand -> moveToGetPassword()

            else -> throw UnsupportedOperationException("This command is not supported")
        }
    }

    private fun showExplanationDialog() =
        SimpleTextBottomSheetDialog.show(
            this@SignUpNameFragment,
            R.string.user_name_restriction_title,
            R.string.user_name_restriction_explanation,
            R.string.understand) {}

    private fun moveToSelectMethod() = findNavController().navigate(R.id.action_signUpNameFragment2_to_signUpSelectMethodFragment)

    private fun moveToGetPassword() = findNavController().navigate(R.id.action_signUpNameFragment2_to_signUpCreatePasswordFragment2)
}