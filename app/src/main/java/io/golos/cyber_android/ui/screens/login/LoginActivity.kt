package io.golos.cyber_android.ui.screens.login

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import io.golos.cyber_android.MainActivity
import io.golos.cyber_android.R
import io.golos.cyber_android.serviceLocator
import io.golos.cyber_android.ui.base.BaseActivity
import io.golos.domain.model.SignInState
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        setupViewModel()
    }

    private fun setupViewModel() {
        val viewModel = ViewModelProviders.of(
            this,
            serviceLocator
                .getAuthViewModelFactory()
        ).get(AuthViewModel::class.java)

        viewModel.authLiveData.observe(this, Observer {
            if (it == SignInState.LOG_IN_NEEDED && postNavHost.findNavController().currentDestination == null)
                initAuthFlow()
            if (it == SignInState.LOADING)
                onLoading()
            if (it == SignInState.USER_LOGGED_IN)
                navigateToMainScreen()
        })
    }

    private fun initAuthFlow() {
        postNavHost.findNavController().setGraph(R.navigation.graph_login)
    }

    private fun onLoading() {

    }

    private fun navigateToMainScreen() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
