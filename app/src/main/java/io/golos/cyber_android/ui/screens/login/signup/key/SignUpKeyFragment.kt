package io.golos.cyber_android.ui.screens.login.signup.key

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import io.golos.cyber_android.R
import io.golos.cyber_android.serviceLocator
import io.golos.cyber_android.ui.screens.login.signup.SignUpViewModel
import kotlinx.android.synthetic.main.fragment_sign_up_key.*

class SignUpKeyFragment : Fragment() {

    private lateinit var viewModel: SignUpViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_sign_up_key, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupViewModel()

        download.setOnClickListener {  }
    }

    private fun setupViewModel() {
        viewModel = ViewModelProviders.of(
            requireActivity(),
            requireContext()
                .serviceLocator
                .getSignUpViewModelFactory()
        ).get(SignUpViewModel::class.java)
    }

}
