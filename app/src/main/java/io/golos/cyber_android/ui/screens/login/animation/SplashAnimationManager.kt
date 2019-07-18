package io.golos.cyber_android.ui.screens.login.animation

import io.golos.domain.requestmodel.SignInState

/**
 * Makes a decision when to start and when to stop splash animation
 */
class SplashAnimationManager(private var target: SplashAnimationManagerTarget?) {
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
                        target?.startSplashAnimation()
                        States.Loading
                    }
                    SignInState.LOG_IN_NEEDED -> {
                        target?.completeSplashAnimation()
                        States.AuthInProgress
                    }
                    else -> currentState
                }
            }
            States.Loading -> {
                when(event) {
                    SignInState.LOG_IN_NEEDED -> {
                        target?.completeSplashAnimation()
                        States.AuthInProgress
                    }
                    SignInState.USER_LOGGED_IN_PIN_SET,
                    SignInState.USER_LOGGED_IN_PIN_NOT_SET -> {
                        target?.completeSplashAnimation()
                        currentState
                    }
                    else -> currentState
                }
            }
            States.AuthInProgress,
            States.NavigatingToMain -> currentState
        }
    }

    fun clear() {
        target = null
    }
}