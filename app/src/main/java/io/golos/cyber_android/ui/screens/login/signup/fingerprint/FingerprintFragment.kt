package io.golos.cyber_android.ui.screens.login.signup.fingerprint


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import io.golos.cyber_android.R
import io.golos.cyber_android.serviceLocator
import io.golos.cyber_android.ui.common.helper.UIHelper
import io.golos.cyber_android.ui.common.mvvm.ShowMessageCommand
import kotlinx.android.synthetic.main.fragment_fingerprint.*

class FingerprintFragment : Fragment() {
    private lateinit var viewModel: FingerprintViewModel

    private val uiHelper: UIHelper by lazy { requireContext().serviceLocator.uiHelper }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_fingerprint, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewModel()
        observeViewModel()

        unlockFingerprintButton.setOnClickListener {
            viewModel.onUnlockViaFingerprintClick()
        }

        unlockPasscodeButton.setOnClickListener {
            viewModel.onUnlockViaPinCodeClick()
        }
    }

    private fun setupViewModel() {
        viewModel = ViewModelProviders.of(
            this,
            requireActivity().serviceLocator.getDefaultViewModelFactory()
        )
        .get(FingerprintViewModel::class.java)
    }

    private fun observeViewModel() {
        viewModel.command.observe(this, Observer { command ->
            when(command) {
                is ShowMessageCommand -> uiHelper.showMessage(command.textResId)
            }
        })
    }
}
