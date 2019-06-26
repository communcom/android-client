package io.golos.cyber_android.ui.screens.login

import android.view.View
import io.golos.domain.requestmodel.SignInState

class SplashManager(private val visibilityAction: (Int) -> Unit) {
    private enum class States {
        Start,
        Loading,
        AuthInProgress,
        NavigatingToMain
    }

    private var currentState = States.Start

    fun processEvent(event: SignInState) {
        currentState = when(currentState) {
            States.Start -> {
                when(event) {
                    SignInState.LOADING -> {
                        visibilityAction(View.VISIBLE)
                        States.Loading
                    }
                    SignInState.LOG_IN_NEEDED -> {
                        visibilityAction(View.INVISIBLE)
                        States.AuthInProgress
                    }
                    else -> currentState
                }
            }
            States.Loading -> {
                when(event) {
                    SignInState.LOG_IN_NEEDED -> {
                        visibilityAction(View.INVISIBLE)
                        States.AuthInProgress
                    }
                    else -> currentState
                }
            }
            States.AuthInProgress,
            States.NavigatingToMain -> currentState
        }
    }
}