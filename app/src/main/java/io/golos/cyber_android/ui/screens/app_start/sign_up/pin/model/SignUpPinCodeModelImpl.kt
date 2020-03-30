package io.golos.cyber_android.ui.screens.app_start.sign_up.pin.model

import io.golos.cyber_android.ui.screens.app_start.sign_in.pin.model.pin_code_storage.PinCodesStorage
import io.golos.use_cases.sign_up.core.SignUpCoreView
import io.golos.use_cases.sign_up.core.data_structs.PinCodeEntered
import javax.inject.Inject

class SignUpPinCodeModelImpl
@Inject
constructor(
    private val pinCodeStorage: PinCodesStorage,
    private val signUpCore: SignUpCoreView
) : SignUpPinCodeModel {

    override fun updatePrimaryCode(code: String?) = pinCodeStorage.updatePrimaryCode(code)

    override fun updateRepeatedCode(code: String?) = pinCodeStorage.updateRepeatedCode(code)

    override fun isPrimaryCodeCompleted() = pinCodeStorage.isPrimaryCodeCompleted()

    override fun isRepeatedCodeCompleted() = pinCodeStorage.isRepeatedCodeCompleted()

    /**
     * @return true if valid
     */
    override fun validate() = pinCodeStorage.validate()

    override suspend fun saveCode(): Boolean {
        signUpCore.process(PinCodeEntered(pinCodeStorage.pinCode!!))
        return true
    }
}