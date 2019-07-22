package io.golos.cyber_android.application.dependency_injection.login_activity

import dagger.Binds
import dagger.Module
import io.golos.cyber_android.ui.screens.login.signup.fingerprint.FingerprintModel
import io.golos.cyber_android.ui.screens.login.signup.fingerprint.FingerprintModelImpl
import io.golos.cyber_android.ui.screens.login.signup.pin.PinCodeModel
import io.golos.cyber_android.ui.screens.login.signup.pin.PinCodeModelImpl

@Module
abstract class LoginActivityModuleBinds {
    @Binds
    abstract fun providePinCodeModel(model: PinCodeModelImpl): PinCodeModel

    @Binds
    abstract fun provideFingerprintModel(model: FingerprintModelImpl): FingerprintModel
}