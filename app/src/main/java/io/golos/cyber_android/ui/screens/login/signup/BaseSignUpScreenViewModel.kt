package io.golos.cyber_android.ui.screens.login.signup

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/**
 * Base [ViewModel] for sign up screens. Allows to store and validate one String type [field].
 * Corresponding view model needs to implement [validate] method in order to correctly validate data stored in [field].
 */
abstract class BaseSignUpScreenViewModel: ViewModel() {

    private val validnessLiveData = MutableLiveData(false)

    /**
     * [LiveData] that represents validness of the [field]
     */
    val getValidnessLiveData = validnessLiveData as LiveData<Boolean>

    private var field = ""

    /**
     * Returns current username if it's valid, otherwise returns null
     */
    fun getFieldIfValid() = if (validate(field)) field else null

    /**
     * Saves username into this ViewModel and validates it
     */
    fun onFieldChanged(field: String) {
        this.field = field.trim()
        validnessLiveData.postValue(validate(this.field))
    }

    abstract fun validate(field: String): Boolean
}