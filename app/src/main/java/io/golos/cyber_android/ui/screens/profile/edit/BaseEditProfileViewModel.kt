package io.golos.cyber_android.ui.screens.profile.edit

import androidx.lifecycle.ViewModel
import io.golos.domain.interactors.user.UserMetadataUseCase

abstract class BaseEditProfileViewModel(private val userMetadataUseCase: UserMetadataUseCase) : ViewModel() {

    val getMetadataLiveData = userMetadataUseCase.getAsLiveData

    val getMetadataUpdateStateLiveData = userMetadataUseCase.getUpdateResultLiveData

    init {
        userMetadataUseCase.subscribe()
    }

    override fun onCleared() {
        super.onCleared()
        userMetadataUseCase.unsubscribe()
    }
}
