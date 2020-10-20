package io.golos.cyber_android.ui.screens.app_start.sign_in.pin.view

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintSet
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.transition.TransitionManager
import io.golos.cyber_android.R
import io.golos.cyber_android.application.App
import io.golos.cyber_android.ui.screens.app_start.sign_in.pin.di.SignInPinCodeFragmentComponent
import io.golos.cyber_android.ui.screens.app_start.sign_in.pin.dto.NavigateToFingerprintCommand
import io.golos.cyber_android.ui.screens.app_start.sign_in.pin.view_model.PinCodeViewModelBase
import io.golos.cyber_android.ui.screens.app_start.sign_in.pin.view_model.SignInPinCodeViewModel
import io.golos.cyber_android.ui.screens.main_activity.MainActivity
import io.golos.cyber_android.ui.shared.base.FragmentBaseCoroutines
import io.golos.cyber_android.ui.shared.mvvm.viewModel.FragmentViewModelFactory
import io.golos.cyber_android.ui.shared.mvvm.view_commands.NavigateToMainScreenCommand
import io.golos.cyber_android.ui.shared.mvvm.view_commands.ViewCommand
import kotlinx.android.synthetic.main.fragment_pin_code.*
import javax.inject.Inject

open class SignInPinCodeFragment : FragmentBaseCoroutines() {
    override val isBackHandlerEnabled: Boolean = true

    protected lateinit var viewModel: PinCodeViewModelBase

    @Inject
    internal lateinit var viewModelFactory: FragmentViewModelFactory

    override fun inject(key: String) = App.injections.get<SignInPinCodeFragmentComponent>(key).inject(this)

    override fun releaseInjection(key: String) = App.injections.release<SignInPinCodeFragmentComponent>(key)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_pin_code, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewModel()
        observeViewModel()

        keypad.setOnDigitKeyPressListener { digit ->
            when {
                primaryCode.isActive -> viewModel.onPrimaryCodeUpdated(primaryCode.setDigit(digit))
                repeatedCode.isActive  -> viewModel.onRepeatedCodeUpdated(repeatedCode.setDigit(digit))
            }
        }

        keypad.setOnClearKeyPressListener {
            viewModel.onClearButtonClick()
        }
    }

    protected open fun setupViewModel() {
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(SignInPinCodeViewModel::class.java)
    }

    private fun observeViewModel() {
        viewModel.isInExtendedMode.observe(viewLifecycleOwner, Observer { isInExtendedMode ->
            if(isInExtendedMode) {
                moveToExtendedMode()        // Show widget for password validation
            } else {
                moveToSimpleMode()
            }
        })

        viewModel.codeState.observe(viewLifecycleOwner, Observer { codeState ->
            primaryCode.isActive = codeState.isPrimaryCodeActive
            repeatedCode.isActive = codeState.isRepeatedCodeActive

            if(codeState.resetNeeded) {
                primaryCode.reset()
                repeatedCode.reset()
            }
        })

        viewModel.command.observe(viewLifecycleOwner, Observer { processViewCommandGeneral(it) })
    }

    private fun moveToExtendedMode() {
        val newMode = ConstraintSet()
        newMode.clone(requireContext(), R.layout.fragment_pin_code_ext)

        TransitionManager.beginDelayedTransition(root)
        newMode.applyTo(root)

        primaryCode.isActive = false
        repeatedCode.isActive = true
    }

    private fun moveToSimpleMode() {
        val newMode = ConstraintSet()
        newMode.clone(requireContext(), R.layout.fragment_pin_code)

        TransitionManager.beginDelayedTransition(root)
        newMode.applyTo(root)

        primaryCode.isActive = true
        repeatedCode.isActive = false
    }

    override fun processViewCommand(command: ViewCommand) {
        when(command) {
            is NavigateToFingerprintCommand -> findNavController().navigate(R.id.action_signInPinCodeFragment_to_signInAppUnlockFragment)
            is NavigateToMainScreenCommand -> {
                startActivity(Intent(requireContext(), MainActivity::class.java))
                requireActivity().finish()
            }
        }
    }
}
