package io.golos.cyber_android.ui.screens.login

import android.util.Log
import io.golos.domain.requestmodel.SignInState

class SplashManager(private var animationTarget: SplashAnimationTarget?) {
    private enum class States {
        Start,
        Loading,
        AuthInProgress,
        NavigatingToMain
    }

    private var currentState = States.Start

    fun processEvent(event: SignInState) {
        Log.d("ROTATION", "From: $currentState on $event")

        currentState = when(currentState) {
            States.Start -> {
                when(event) {
                    SignInState.LOADING -> {
                        animationTarget?.startSplashAnimation()
                        States.Loading
                    }
                    SignInState.LOG_IN_NEEDED -> {
                        animationTarget?.completeSplashAnimation()
                        States.AuthInProgress
                    }
                    else -> currentState
                }
            }
            States.Loading -> {
                when(event) {
                    SignInState.LOG_IN_NEEDED -> {
                        animationTarget?.completeSplashAnimation()
                        States.AuthInProgress
                    }
                    SignInState.USER_LOGGED_IN -> {
                        animationTarget?.completeSplashAnimation()
                        currentState
                    }
                    else -> currentState
                }
            }
            States.AuthInProgress,
            States.NavigatingToMain -> currentState
        }
    }

    fun clearAnimationTarget() {
        animationTarget = null
    }
}