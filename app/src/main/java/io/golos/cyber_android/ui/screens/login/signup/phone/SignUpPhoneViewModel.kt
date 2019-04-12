package io.golos.cyber_android.ui.screens.login.signup.phone

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SignUpPhoneViewModel: ViewModel() {

    private val validnessLiveData = MutableLiveData(false)

    /**
     * [LiveData] that represents validness of the [phone]
     */
    val getValidnessLiveData = validnessLiveData as LiveData<Boolean>

    private var phone = ""

    fun getPhoneIfValid() = if (validate(phone)) phone else null

    /**
     * Saves phone into this ViewModel and validates it
     */
    fun onPhoneChanged(phone: String) {
        this.phone = phone.trim()
        validnessLiveData.postValue(validate(this.phone))
    }

    private fun validate(phone: String): Boolean {
        return phone.length > 3
    }
}