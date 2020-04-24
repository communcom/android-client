package io.golos.cyber_android.ui.screens.app_start.sign_up.select_method

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import dagger.Lazy
import io.golos.cyber_android.R
import io.golos.cyber_android.application.App
import io.golos.cyber_android.ui.screens.app_start.sign_in.activity.SignInActivity
import io.golos.cyber_android.ui.screens.app_start.sign_up.shared.SignUpDescriptionHelper
import io.golos.cyber_android.ui.screens.app_start.sign_up.shared.messages_mapper.SignUpMessagesMapper
import io.golos.cyber_android.ui.screens.app_start.sign_up.select_method.di.SignUpSelectMethodFragmentComponent
import io.golos.cyber_android.ui.screens.app_start.sign_up.select_method.social_network_auth_providers.SocialNetworkAuthProvider
import io.golos.cyber_android.ui.shared.base.FragmentBaseCoroutines
import io.golos.cyber_android.ui.shared.mvvm.viewModel.ActivityViewModelFactory
import io.golos.domain.analytics.AnalyticsFacade
import io.golos.domain.dependency_injection.Clarification
import io.golos.use_cases.sign_up.core.SignUpCoreView
import io.golos.use_cases.sign_up.core.data_structs.*
import kotlinx.android.synthetic.main.fragment_sign_up_phone.header
import kotlinx.android.synthetic.main.fragment_sign_up_select_method.*
import kotlinx.android.synthetic.main.fragment_sign_up_select_method.signUpDescription
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

class SignUpSelectMethodFragment: FragmentBaseCoroutines() {
    override val isBackHandlerEnabled: Boolean = true

    @Inject
    @field:Named(Clarification.GOOGLE)
    internal lateinit var googleAuth: Lazy<SocialNetworkAuthProvider>

    @Inject
    @field:Named(Clarification.FACEBOOK)
    internal lateinit var facebookAuth: Lazy<SocialNetworkAuthProvider>

    @Inject
    internal lateinit var analyticsFacade: AnalyticsFacade

    @Inject
    internal lateinit var singUpCore: SignUpCoreView

    @Inject
    internal lateinit var signUpMessagesMapper: Lazy<SignUpMessagesMapper>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        analyticsFacade.openScreen110()
    }

    override fun inject(key: String) = App.injections.get<SignUpSelectMethodFragmentComponent>(key).inject(this)

    override fun releaseInjection(key: String) = App.injections.release<SignUpSelectMethodFragmentComponent>(key)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_sign_up_select_method, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        header.setOnBackButtonClickListener { findNavController().navigateUp() }

        phoneButton.setOnClickListener { singUpCore.process(PhoneSelected()) }
        emailButton.setOnClickListener { singUpCore.process(EmailSelected()) }

        googleButton.setOnClickListener {
            analyticsFacade.openScreen121()
            singUpCore.process(GoogleSelected())
        }

        facebookButton.setOnClickListener {
            analyticsFacade.openScreen131()
            singUpCore.process(FbSelected())
        }

        tvSignIn.setOnClickListener { moveToSignUp() }

        SignUpDescriptionHelper.formSignUpDescription(this, signUpDescription)

        launch {
            singUpCore.commands.collect { processSignUpCommand(it) }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(!googleAuth.get().processActivityResult(requestCode, resultCode, data)) {
            facebookAuth.get().processActivityResult(requestCode, resultCode, data)
        }
    }

    private fun processSignUpCommand(command: SignUpCommand) {
        when(command) {
            is StartGoogleSignIn -> googleAuth.get().startAuth(this)
            is StartFbSignIn -> facebookAuth.get().startAuth(this)
            is NavigateToPhone -> findNavController().navigate(R.id.action_signUpSelectMethodFragment_to_signUpPhoneFragment2)
            is NavigateToEmail -> findNavController().navigate(R.id.action_signUpSelectMethodFragment_to_signUpEmailFragment)
            is ShowError -> uiHelper.showMessage(signUpMessagesMapper.get().getMessage(command.errorCode))
            is ShowLoading -> showLoading()
            is HideLoading -> hideLoading()
            is NavigateToUserName -> findNavController().navigate(R.id.action_signUpSelectMethodFragment_to_signUpNameFragment2)
            else -> throw UnsupportedOperationException("This command is not supported: ${command::class.simpleName}")
        }
    }

    private fun moveToSignUp() {
        val intent = Intent(requireContext(), SignInActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }
}