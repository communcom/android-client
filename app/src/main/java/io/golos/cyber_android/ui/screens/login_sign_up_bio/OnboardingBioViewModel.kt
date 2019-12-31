package io.golos.cyber_android.ui.screens.login_sign_up_bio

import androidx.lifecycle.MutableLiveData
import io.golos.cyber_android.ui.trash.EditProfileViewModelBase
import io.golos.domain.use_cases.user.UserMetadataUseCase
import javax.inject.Inject

class OnboardingBioViewModel
@Inject
constructor(
    private val userMetadataUseCase: UserMetadataUseCase
) : EditProfileViewModelBase(userMetadataUseCase) {

    companion object {
        const val MAX_BIO_LENGTH = 100
    }

    private val validnessLiveData = MutableLiveData(false)

    private val bioLengthLiveData = MutableLiveData(0)

    private var bio = ""

    fun onBioChanged(field: String) {
        this.bio = field.trim()
        bioLengthLiveData.postValue(this.bio.length)
        validnessLiveData.postValue(validate(this.bio))
    }

    private fun validate(bio: String) = bio.length in 0..MAX_BIO_LENGTH

    fun updateBio(waitForTransaction: Boolean = true) {
        userMetadataUseCase.updateMetadata(newBio = bio, shouldWaitForTransaction =  waitForTransaction)
    }
}
