package io.golos.cyber_android.ui.screens.login_sign_up_select_method

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.safetynet.SafetyNet
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import io.golos.cyber_android.BuildConfig
import io.golos.cyber_android.R
import io.golos.cyber_android.application.App
import io.golos.cyber_android.application.shared.analytics.AnalyticsFacade
import io.golos.cyber_android.ui.screens.login_shared.SignUpDescriptionHelper
import io.golos.cyber_android.ui.screens.login_shared.fragments_data_pass.LoginActivityFragmentsDataPass
import io.golos.cyber_android.ui.screens.login_sign_up.SignUpViewModel
import io.golos.cyber_android.ui.screens.login_sign_up_select_method.di.SignUpSelectMethodFragmentComponent
import io.golos.cyber_android.ui.screens.login_sign_up_select_method.dto.NavigateToUserNameStepCommand
import io.golos.cyber_android.ui.screens.login_sign_up_select_method.social_network_auth_providers.SocialNetworkAuthProvider
import io.golos.cyber_android.ui.shared.base.FragmentBase
import io.golos.cyber_android.ui.shared.mvvm.viewModel.ActivityViewModelFactory
import io.golos.cyber_android.ui.shared.mvvm.view_commands.ViewCommand
import io.golos.domain.dependency_injection.Clarification
import kotlinx.android.synthetic.main.fragment_sign_up_phone.header
import kotlinx.android.synthetic.main.fragment_sign_up_select_method.*
import kotlinx.android.synthetic.main.fragment_sign_up_select_method.signUpDescription
import timber.log.Timber
import java.util.concurrent.Executor
import javax.inject.Inject
import javax.inject.Named


class SignUpSelectMethodFragment: FragmentBase() {
    @Inject
    @field:Named(Clarification.GOOGLE)
    internal lateinit var googleAuth: SocialNetworkAuthProvider

    @Inject
    @field:Named(Clarification.FACEBOOK)
    internal lateinit var facebookAuth: SocialNetworkAuthProvider

    @Inject
    protected lateinit var viewModelFactory: ActivityViewModelFactory

    @Inject
    internal lateinit var analyticsFacade: AnalyticsFacade

    @Inject
    internal lateinit var dataPass: LoginActivityFragmentsDataPass

    private lateinit var signUpViewModel: SignUpViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        analyticsFacade.openScreen110()
    }

    override fun onDestroy() {
        super.onDestroy()

        if(isRemoving) {
            googleAuth.close()
            facebookAuth.close()
        }
    }

    override fun inject(key: String) = App.injections.get<SignUpSelectMethodFragmentComponent>(key).inject(this)

    override fun releaseInjection(key: String) = App.injections.release<SignUpSelectMethodFragmentComponent>(key)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_sign_up_select_method, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        signUpViewModel = ViewModelProviders.of(requireActivity(), viewModelFactory).get(SignUpViewModel::class.java)

        header.setOnBackButtonClickListener { findNavController().navigateUp() }

        phoneButton.setOnClickListener {
            SafetyNet.getClient(requireActivity()).verifyWithRecaptcha(BuildConfig.GOOGLE_RECAPTCHA_KEY)
                .addOnSuccessListener { response ->
                    val userResponseToken = response.tokenResult
                    if (userResponseToken?.isNotEmpty() == true) {
                        dataPass.putCaptcha(userResponseToken)
                        findNavController().navigate(R.id.action_signUpSelectMethod_to_signUpPhoneFragment)
                    }
                }
                .addOnFailureListener { e ->
                    Timber.e(e)
                    uiHelper.showMessage(R.string.common_captcha_error)
                }
        }

        googleButton.setOnClickListener {
            analyticsFacade.openScreen121()
            startAuth(googleAuth)
        }

        facebookButton.setOnClickListener {
            analyticsFacade.openScreen131()
            startAuth(facebookAuth)
        }

        tvSignIn.setOnClickListener { findNavController().navigate(R.id.action_signUpSelectMethod_to_signInFragment) }

        SignUpDescriptionHelper.formSignUpDescription(this, signUpDescription)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(!googleAuth.processActivityResult(requestCode, resultCode, data)) {
            facebookAuth.processActivityResult(requestCode, resultCode, data)
        }
    }

    override fun processViewCommand(command: ViewCommand) {
        when(command) {
            is NavigateToUserNameStepCommand -> {
                signUpViewModel.updateRegisterState(phone = null, identity = command.identity)
                findNavController().navigate(R.id.action_signUpSelectMethod_to_signUpNameFragment)
            }
        }
    }


    private fun startAuth(provider: SocialNetworkAuthProvider) {
        provider.apply {
            command.observe({viewLifecycleOwner.lifecycle}) { processViewCommandGeneral(it) }
            startAuth(this@SignUpSelectMethodFragment)
        }
    }
}