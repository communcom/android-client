package io.golos.cyber_android.ui.screens.login_activity.signup.fragments.key

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import io.golos.cyber_android.R
import io.golos.cyber_android.safeNavigate
import io.golos.cyber_android.serviceLocator
import io.golos.cyber_android.ui.screens.login_activity.AuthViewModel
import kotlinx.android.synthetic.main.fragment_sign_up_key.*


class SignUpKeyFragment : Fragment() {

    private lateinit var authViewModel: AuthViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_sign_up_key, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupViewModel()

        moveToPasscodeButton.setOnClickListener {
            navigateToProtectionStep()
        }

    }

    private fun navigateToProtectionStep() {
        findNavController().safeNavigate(R.id.signUpKeyFragment, R.id.action_signUpKeyFragment_to_pinCodeFragment)
    }

    private fun setupViewModel() {
        authViewModel = ViewModelProviders.of(
            requireActivity(),
            requireContext()
                .serviceLocator
                .getDefaultViewModelFactory()
        ).get(AuthViewModel::class.java)
    }
}
