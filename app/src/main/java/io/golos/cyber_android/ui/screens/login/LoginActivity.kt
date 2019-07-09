package io.golos.cyber_android.ui.screens.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import io.golos.cyber_android.R
import io.golos.cyber_android.serviceLocator
import io.golos.cyber_android.ui.base.BaseActivity
import io.golos.cyber_android.ui.screens.main.MainActivity
import io.golos.domain.requestmodel.SignInState
import kotlinx.android.synthetic.main.activity_login.*
import android.view.animation.LinearInterpolator
import android.view.animation.Animation
import android.view.animation.RotateAnimation



class LoginActivity : BaseActivity(), SplashAnimationTarget {
    private val splashManager = SplashManager(this)

    private lateinit var animation: RotateAnimation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        setupViewModel()
    }

    private fun setupViewModel() {
        val viewModel = ViewModelProviders.of(
            this,
            serviceLocator
                .getDefaultViewModelFactory()
        ).get(AuthViewModel::class.java)

        viewModel.authLiveData.observe(this, Observer {
            splashManager.processEvent(it)

            if (it == SignInState.LOG_IN_NEEDED && postNavHost.findNavController().currentDestination == null)
                initAuthFlow()
            if (it == SignInState.LOADING)
                onLoading()
            if (it == SignInState.USER_LOGGED_IN && postNavHost.findNavController().currentDestination == null)
                navigateToMainScreen()
        })
    }

    private fun initAuthFlow() {
        postNavHost.findNavController().setGraph(R.navigation.graph_login)
    }

    private fun onLoading() {
    }

    private fun navigateToMainScreen() {
        splashIcon.post {
            startActivity(Intent(this, MainActivity::class.java))
            splashManager.clearAnimationTarget()
            finish()
        }
    }

    override fun startSplashAnimation() {
        Log.d("ROTATION", "Start animation")
        root.setBackgroundColor(ContextCompat.getColor(this, R.color.blue))
        splashIcon.visibility = View.VISIBLE

        animation = RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
            .apply {
                duration = 1000
                repeatCount = Animation.INFINITE
                interpolator = LinearInterpolator()
            }

        splashIcon.startAnimation(animation)
    }

    override fun completeSplashAnimation() {
        Log.d("ROTATION", "Stop animation")
        splashIcon.clearAnimation()
        splashIcon.visibility = View.INVISIBLE
    }
}
