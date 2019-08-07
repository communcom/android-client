package io.golos.cyber_android.ui.screens.login_activity.signup.fragments.key

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import io.golos.cyber_android.R
import io.golos.cyber_android.application.App
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.login_activity.LoginActivityComponent
import io.golos.cyber_android.ui.base.FragmentBase
import io.golos.cyber_android.ui.common.extensions.safeNavigate
import io.golos.cyber_android.ui.common.mvvm.viewModel.ActivityViewModelFactory
import io.golos.cyber_android.ui.screens.login_activity.AuthViewModel
import kotlinx.android.synthetic.main.fragment_sign_up_key.*
import javax.inject.Inject


class SignUpKeyFragment : FragmentBase() {

    private lateinit var authViewModel: AuthViewModel

    @Inject
    internal lateinit var viewModelFactory: ActivityViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        App.injections.get<LoginActivityComponent>().inject(this)
    }

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
        authViewModel = ViewModelProviders.of(requireActivity(), viewModelFactory).get(AuthViewModel::class.java)
    }
}
