package io.golos.cyber_android.ui.screens.login.signup.name

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SignUpNameViewModel: ViewModel() {

    companion object {
        /**
         * Exact length of the username that is valid
         */
        const val USERNAME_LENGTH = 12
    }

    private val validnessLiveData = MutableLiveData(false)

    /**
     * [LiveData] that represents validness of the [username]
     */
    val getValidnessLiveData = validnessLiveData as LiveData<Boolean>

    private var username = ""

    /**
     * Saves username into this ViewModel and validates it
     */
    fun onUsernameChanged(phone: String) {
        this.username = phone.trim()
        validnessLiveData.postValue(validate(this.username))
    }

    private fun validate(username: String): Boolean {
        return username.length == USERNAME_LENGTH
    }
}