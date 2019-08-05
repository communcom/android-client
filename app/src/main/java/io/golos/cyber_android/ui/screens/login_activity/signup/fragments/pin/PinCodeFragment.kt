package io.golos.cyber_android.ui.screens.login_activity.signup.fragments.pin

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
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.login_activity.LoginActivityComponent
import io.golos.cyber_android.ui.base.FragmentBase
import io.golos.cyber_android.ui.common.mvvm.viewModel.ActivityViewModelFactory
import io.golos.cyber_android.ui.common.mvvm.view_commands.NavigateToMainScreenCommand
import io.golos.cyber_android.ui.common.mvvm.view_commands.ShowMessageCommand
import io.golos.cyber_android.ui.screens.login_activity.signup.fragments.pin.view_commands.NavigateToFingerprintCommand
import io.golos.cyber_android.ui.screens.login_activity.signup.fragments.pin.view_commands.NavigateToKeysCommand
import io.golos.cyber_android.ui.screens.main_activity.MainActivity
import kotlinx.android.synthetic.main.fragment_pin_code.*
import javax.inject.Inject

class PinCodeFragment : FragmentBase() {
    private lateinit var viewModel: PinCodeViewModel

    @Inject
    internal lateinit var viewModelFactory: ActivityViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        App.injections.get<LoginActivityComponent>().inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_pin_code, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewModel()
        observeViewModel()

        keypad.setOnKeyPressListener { digit ->
            when {
                primaryCode.isActive -> viewModel.onPrimaryCodeUpdated(primaryCode.setDigit(digit))
                repeatedCode.isActive  -> viewModel.onRepeatedCodeUpdated(repeatedCode.setDigit(digit))
            }
        }
    }

    private fun setupViewModel() {
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(PinCodeViewModel::class.java)
    }

    private fun observeViewModel() {
        viewModel.isInExtendedMode.observe(this, Observer { isInExtendedMode ->
            if(isInExtendedMode) {
                moveToExtendedMode()        // Show widget for password validation
            }
        })

        viewModel.codeState.observe(this, Observer { codeState ->
            primaryCode.isActive = codeState.isPrimaryCodeActive
            repeatedCode.isActive = codeState.isRepeatedCodeActive

            primaryCode.isInErrorMode = codeState.isInErrorState
            repeatedCode.isInErrorMode = codeState.isInErrorState

            if(codeState.resetNeeded) {
                primaryCode.reset()
                repeatedCode.reset()
            }
        })

        viewModel.command.observe(this, Observer { command ->
            when(command) {
                is ShowMessageCommand -> uiHelper.showMessage(command.textResId)

                is NavigateToFingerprintCommand -> findNavController().navigate(R.id.action_pinCodeFragment_to_fingerprintFragment)

                is NavigateToKeysCommand -> findNavController().navigate(R.id.action_pinCodeFragment_to_signUpProtectionKeysFragment)

                is NavigateToMainScreenCommand -> {
                    startActivity(Intent(requireContext(), MainActivity::class.java))
                    requireActivity().finish()
                }
            }
        })
    }

    private fun moveToExtendedMode() {
        val newMode = ConstraintSet()
        newMode.clone(requireContext(), R.layout.fragment_pin_code_ext)

        TransitionManager.beginDelayedTransition(root)
        newMode.applyTo(root)

        primaryCode.isActive = false
        repeatedCode.isActive = true
    }
}
