package io.golos.cyber_android.ui.screens.login.signup.key

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import io.golos.cyber_android.R
import io.golos.cyber_android.safeNavigate
import io.golos.cyber_android.serviceLocator
import io.golos.cyber_android.ui.Tags
import io.golos.cyber_android.ui.screens.login.AuthViewModel
import io.golos.cyber_android.ui.screens.login.signup.keys.SignUpProtectionKeysFragment
import io.golos.domain.interactors.model.UserAuthState
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

        authViewModel.authStateLiveData.observe(this, Observer { authState ->
            if (authState.isUserLoggedIn)
                moveToPasscodeButton.setOnClickListener {
                    navigateToProtectionStep(authState)
                }     // to passcode!!!
            else Toast.makeText(requireContext(), R.string.unknown_error, Toast.LENGTH_SHORT).show()
        })
    }

    private fun navigateToProtectionStep(authState: UserAuthState) {
        findNavController().safeNavigate(
            R.id.signUpKeyFragment,
            R.id.action_signUpKeyFragment_to_signUpProtectionKeysFragment,
            Bundle().apply {
                putString(
                    Tags.ARGS,
                    requireContext()
                        .serviceLocator.moshi
                        .adapter(SignUpProtectionKeysFragment.Args::class.java)
                        .toJson(SignUpProtectionKeysFragment.Args(authState.userName))
                )
            }
        )
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
