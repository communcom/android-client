package io.golos.cyber_android.ui.screens.login_sign_up_select_method

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import io.golos.cyber_android.R
import kotlinx.android.synthetic.main.fragment_sign_up_phone.header
import kotlinx.android.synthetic.main.fragment_sign_up_select_method.*

class SignUpSelectMethodFragment: Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_sign_up_select_method, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        header.setOnBackButtonClickListener { findNavController().navigateUp() }

        phoneButton.setOnClickListener { findNavController().navigate(R.id.action_signUpSelectMethod_to_signUpPhoneFragment) }
        googleButton.setOnClickListener {  }
        facebookButton.setOnClickListener {  }

        tvSignIn.setOnClickListener { findNavController().navigate(R.id.action_signUpSelectMethod_to_signInFragment) }
    }
}