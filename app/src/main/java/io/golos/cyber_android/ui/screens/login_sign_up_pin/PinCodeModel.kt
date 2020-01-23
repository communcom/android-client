package io.golos.cyber_android.ui.screens.login_sign_up_pin

import io.golos.domain.dto.AuthType

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

    suspend fun saveKeysExported()
}