package io.golos.cyber_android.ui.screens.login_sign_up_bio

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.golos.domain.use_cases.user.UserMetadataUseCase
import javax.inject.Inject

class OnboardingBioViewModel
@Inject
constructor(
    private val userMetadataUseCase: UserMetadataUseCase
) : ViewModel() {

    companion object {
        const val MAX_BIO_LENGTH = 100
    }

    private val validnessLiveData = MutableLiveData(false)

    private val bioLengthLiveData = MutableLiveData(0)

    private var bio = ""

    val getMetadataUpdateStateLiveData = userMetadataUseCase.getUpdateResultLiveData

    init {
        userMetadataUseCase.subscribe()
    }

    override fun onCleared() {
        userMetadataUseCase.unsubscribe()
        super.onCleared()
    }

    fun onBioChanged(field: String) {
        this.bio = field.trim()
        bioLengthLiveData.postValue(this.bio.length)
        validnessLiveData.postValue(validate(this.bio))
    }

    fun updateBio(waitForTransaction: Boolean = true) {
        userMetadataUseCase.updateMetadata(newBio = bio, shouldWaitForTransaction =  waitForTransaction)
    }

    private fun validate(bio: String) = bio.length in 0..MAX_BIO_LENGTH
}
