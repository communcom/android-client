package io.golos.cyber_android.ui.screens.app_start.sign_in.pin.model.pin_code_storage

interface PinCodesStorage {
    val pinCode: String?

    fun updatePrimaryCode(code: String?)

    fun updateRepeatedCode(code: String?)

    fun isPrimaryCodeCompleted(): Boolean

    fun isRepeatedCodeCompleted(): Boolean

    /**
     * @return true if valid
     */
    fun validate(): Boolean
}