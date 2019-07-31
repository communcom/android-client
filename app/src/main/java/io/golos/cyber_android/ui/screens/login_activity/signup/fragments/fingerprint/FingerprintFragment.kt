package io.golos.cyber_android.ui.screens.login_activity.signup.fragments.fingerprint


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import io.golos.cyber_android.R
import io.golos.cyber_android.serviceLocator
import io.golos.cyber_android.ui.common.helper.UIHelper
import io.golos.cyber_android.ui.common.mvvm.view_commands.ShowMessageCommand
import io.golos.cyber_android.ui.screens.login_activity.signup.fragments.fingerprint.view_commands.NavigateToKeysCommand
import io.golos.cyber_android.ui.common.mvvm.view_commands.NavigateToMainScreenCommand
import io.golos.cyber_android.ui.screens.main_activity.MainActivity
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

                is NavigateToKeysCommand -> findNavController().navigate(R.id.action_fingerprintFragment_to_signUpProtectionKeysFragment)

                is NavigateToMainScreenCommand -> {
                    startActivity(Intent(requireContext(), MainActivity::class.java))
                    requireActivity().finish()
                }
            }
        })
    }
}
