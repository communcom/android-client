package io.golos.cyber_android.ui.screens.app_start.sign_in.pin.model

interface SignInPinCodeModel : PinCodeModel {
    val isFingerprintAuthenticationPossible: Boolean

    suspend fun saveKeysExported()
}