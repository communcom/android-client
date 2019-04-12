package io.golos.cyber_android.ui.screens.login.signup.verification

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SignUpVerificationViewModel: ViewModel() {

    private val validnessLiveData = MutableLiveData(false)

    /**
     * [LiveData] that represents validness of the [code]
     */
    val getValidnessLiveData = validnessLiveData as LiveData<Boolean>

    private var code = ""

    fun getCodeIfValid() = if (validate(code)) code else null

    /**
     * Saves code into this ViewModel and validates it
     */
    fun onCodeChanged(code: String) {
        this.code = code
        validnessLiveData.postValue(validate(code))
    }

    private fun validate(code: String): Boolean {
        return code.length == 4
    }

}