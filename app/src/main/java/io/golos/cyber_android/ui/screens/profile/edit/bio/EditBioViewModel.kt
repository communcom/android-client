package io.golos.cyber_android.ui.screens.profile.edit.bio

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class EditBioViewModel : ViewModel() {

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

    /**
     * Returns current username if it's valid, otherwise returns null
     */
    fun getBioIfValid() = if (validate(bio)) bio else null

    /**
     * Saves username into this ViewModel and validates it
     */
    fun onBioChanged(field: String) {
        this.bio = field.trim()
        bioLengthLiveData.postValue(this.bio.length)
        validnessLiveData.postValue(validate(this.bio))
    }

    private fun validate(bio: String) = bio.length in 5..MAX_BIO_LENGTH
}
