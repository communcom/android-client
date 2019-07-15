package io.golos.cyber_android.ui.screens.login.pin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.transition.TransitionManager
import io.golos.cyber_android.R
import io.golos.cyber_android.serviceLocator
import io.golos.cyber_android.ui.common.helper.UIHelper
import kotlinx.android.synthetic.main.fragment_pin_code.*

class PinCodeFragment : Fragment() {
    private lateinit var viewModel: PinCodeViewModel

    private val uiHelper: UIHelper by lazy { requireContext().serviceLocator.uiHelper }

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
        viewModel = ViewModelProviders.of(
            this,
            requireActivity().serviceLocator.getDefaultViewModelFactory()
        )
        .get(PinCodeViewModel::class.java)
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

        viewModel.error.observe(this, Observer { errorResId ->
            uiHelper.showMessage(errorResId)
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
