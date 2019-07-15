package io.golos.cyber_android.ui.screens.login.pin

interface PinCodeModel {
    fun updatePrimaryCode(code: String?)

    fun updateRepeatedCode(code: String?)

    fun isPrimaryCodeCompleted(): Boolean

    fun isRepeatedCodeCompleted(): Boolean

    /**
     * @return true if valid
     */
    fun validate(): Boolean

    suspend fun saveCode()
}