package io.golos.cyber_android.ui.screens.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import io.golos.domain.interactors.sign.SignInUseCase

open class AuthViewModel(private val signInUseCase: SignInUseCase) : ViewModel() {

    /**
     * [LiveData] that indicates current state of auth process
     */
    val authLiveData = signInUseCase.getSignInState

    init {
        signInUseCase.subscribe()
    }

    override fun onCleared() {
        super.onCleared()
        signInUseCase.unsubscribe()
    }
}