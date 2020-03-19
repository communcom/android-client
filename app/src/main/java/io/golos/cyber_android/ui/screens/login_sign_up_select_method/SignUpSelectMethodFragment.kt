package io.golos.cyber_android.ui.screens.login_sign_up_select_method

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import dagger.Lazy
import io.golos.cyber_android.R
import io.golos.cyber_android.application.App
import io.golos.cyber_android.ui.screens.login_shared.SignUpDescriptionHelper
import io.golos.cyber_android.ui.screens.login_sign_up_select_method.di.SignUpSelectMethodFragmentComponent
import io.golos.cyber_android.ui.screens.login_sign_up_select_method.google.GoogleAuth
import io.golos.cyber_android.ui.shared.base.FragmentBase
import kotlinx.android.synthetic.main.fragment_sign_up_phone.header
import kotlinx.android.synthetic.main.fragment_sign_up_select_method.*
import kotlinx.android.synthetic.main.fragment_sign_up_select_method.signUpDescription
import javax.inject.Inject

class SignUpSelectMethodFragment: FragmentBase() {
    @Inject
    internal lateinit var googleAuth: Lazy<GoogleAuth>

    override fun onDestroy() {
        super.onDestroy()

        if(isRemoving) {
            googleAuth.get().close()
        }
    }

    override fun inject(key: String) = App.injections.get<SignUpSelectMethodFragmentComponent>(key).inject(this)

    override fun releaseInjection(key: String) = App.injections.release<SignUpSelectMethodFragmentComponent>(key)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_sign_up_select_method, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        header.setOnBackButtonClickListener { findNavController().navigateUp() }

        phoneButton.setOnClickListener { findNavController().navigate(R.id.action_signUpSelectMethod_to_signUpPhoneFragment) }

        googleButton.setOnClickListener {
            googleAuth.get().apply {
               command.observe({viewLifecycleOwner.lifecycle}) { processViewCommandGeneral(it) }
                startAuth(this@SignUpSelectMethodFragment)
            }
        }

        facebookButton.setOnClickListener {  }

        tvSignIn.setOnClickListener { findNavController().navigate(R.id.action_signUpSelectMethod_to_signInFragment) }

        SignUpDescriptionHelper.formSignUpDescription(this, signUpDescription)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        googleAuth.get().processActivityResult(requestCode, resultCode, data)
    }
}