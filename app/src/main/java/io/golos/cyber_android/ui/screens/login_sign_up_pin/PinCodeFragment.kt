package io.golos.cyber_android.ui.screens.login_sign_up_pin

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
import io.golos.cyber_android.ui.screens.login_activity.di.LoginActivityComponent
import io.golos.cyber_android.ui.shared.base.FragmentBase
import io.golos.cyber_android.ui.shared.mvvm.viewModel.ActivityViewModelFactory
import io.golos.cyber_android.ui.shared.mvvm.view_commands.NavigateToMainScreenCommand
import io.golos.cyber_android.ui.shared.mvvm.view_commands.ShowMessageResCommand
import io.golos.cyber_android.ui.screens.login_sign_up_pin.view_commands.NavigateToFingerprintCommand
import io.golos.cyber_android.ui.screens.login_sign_up_pin.view_commands.NavigateToKeysCommand
import io.golos.cyber_android.ui.screens.main_activity.MainActivity
import kotlinx.android.synthetic.main.fragment_pin_code.*
import javax.inject.Inject

class PinCodeFragment : FragmentBase() {
    private lateinit var viewModel: PinCodeViewModel

    @Inject
    internal lateinit var viewModelFactory: ActivityViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        App.injections.getBase<LoginActivityComponent>().inject(this)
    }

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

    private fun setupViewModel() {
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(PinCodeViewModel::class.java)
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

        viewModel.command.observe(viewLifecycleOwner, Observer { command ->
            when(command) {
                is ShowMessageResCommand -> uiHelper.showMessage(command.textResId, command.isError)

                is NavigateToFingerprintCommand -> findNavController().navigate(R.id.action_pinCodeFragment_to_fingerprintFragment)

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

    private fun moveToSimpleMode() {
        val newMode = ConstraintSet()
        newMode.clone(requireContext(), R.layout.fragment_pin_code)

        TransitionManager.beginDelayedTransition(root)
        newMode.applyTo(root)

        primaryCode.isActive = true
        repeatedCode.isActive = false
    }
}
