package io.golos.cyber_android.ui.screens.login.signup.onboarding.image

import androidx.lifecycle.ViewModel
import io.golos.domain.interactors.user.UserMetadataUseCase
import javax.inject.Inject

class OnboardingUserImageViewModel
@Inject
constructor(
    private val userMetadataUseCase: UserMetadataUseCase
) : ViewModel() {

    val getUserMetadataLiveData = userMetadataUseCase.getAsLiveData

    init {
        userMetadataUseCase.subscribe()
    }

    override fun onCleared() {
        super.onCleared()
        userMetadataUseCase.unsubscribe()
    }
}
