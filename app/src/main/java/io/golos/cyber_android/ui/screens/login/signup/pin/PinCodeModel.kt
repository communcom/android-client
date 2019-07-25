package io.golos.cyber_android.ui.screens.login.signup.pin

import io.golos.domain.entities.AuthType

interface PinCodeModel {
    val isFingerprintAuthenticationPossible: Boolean

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


    suspend fun getAuthType(): AuthType
}