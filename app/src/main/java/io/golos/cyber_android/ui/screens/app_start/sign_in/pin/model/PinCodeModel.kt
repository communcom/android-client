package io.golos.cyber_android.ui.screens.app_start.sign_in.pin.model

interface PinCodeModel {
    fun updatePrimaryCode(code: String?)

    fun updateRepeatedCode(code: String?)

    fun isPrimaryCodeCompleted(): Boolean

    fun isRepeatedCodeCompleted(): Boolean

    /**
     * @return true if valid
     */
    fun validate(): Boolean

    /**
     * @return true in case of success
     */
    suspend fun saveCode(): Boolean
}