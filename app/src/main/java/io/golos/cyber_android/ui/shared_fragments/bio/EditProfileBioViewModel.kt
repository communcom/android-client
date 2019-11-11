package io.golos.cyber_android.ui.shared_fragments.bio

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.golos.cyber_android.ui.screens.profile.edit.EditProfileViewModelBase
import io.golos.domain.use_cases.user.UserMetadataUseCase
import javax.inject.Inject

class EditProfileBioViewModel
@Inject
constructor(
    private val userMetadataUseCase: UserMetadataUseCase
) : EditProfileViewModelBase(userMetadataUseCase) {

    companion object {
        const val MAX_BIO_LENGTH = 100
    }

    private val validnessLiveData = MutableLiveData(false)

    /**
     * [LiveData] that represents validness of the [bio]
     */
    val getValidnessLiveData = validnessLiveData as LiveData<Boolean>


    private val bioLengthLiveData = MutableLiveData(0)

    val getBioLengthLiveData = bioLengthLiveData as LiveData<Int>

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
