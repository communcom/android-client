package io.golos.cyber_android.ui.screens.login.signup.key

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import io.golos.cyber_android.R
import io.golos.cyber_android.safeNavigate
import io.golos.cyber_android.serviceLocator
import io.golos.cyber_android.ui.Tags
import io.golos.cyber_android.ui.screens.login.AuthViewModel
import io.golos.cyber_android.ui.screens.login.signup.onboarding.image.OnboardingUserImageFragment
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

        moveToPasscodeButton.setOnClickListener { /*navigateToOnboarding()*/ }     // to passcode!!!
    }

    private fun navigateToOnboarding() {
        authViewModel.authStateLiveData.value?.let { auth ->
            findNavController().safeNavigate(
                R.id.signUpKeyFragment,
                R.id.action_signUpKeyFragment_to_onboardingUserImageFragment,
                Bundle().apply {
                    putString(
                        Tags.ARGS,
                        requireContext()
                            .serviceLocator.moshi
                            .adapter(OnboardingUserImageFragment.Args::class.java)
                            .toJson(OnboardingUserImageFragment.Args(auth.userName))
                    )
                }
            )
        }
    }
}
