package io.golos.data.api.registration

import io.golos.commun4j.services.model.FirstRegistrationStepResult
import io.golos.commun4j.services.model.RegisterResult
import io.golos.commun4j.services.model.ResultOk
import io.golos.commun4j.services.model.UserRegistrationStateResult

interface RegistrationApi {
    fun getRegistrationState(phone: String): UserRegistrationStateResult

    fun firstUserRegistrationStep(
        phone: String,
        testingPass: String?
    ): FirstRegistrationStepResult

    fun verifyPhoneForUserRegistration(phone: String, code: Int): ResultOk

    fun setVerifiedUserName(user: String, phone: String): ResultOk

    fun writeUserToBlockChain(
        userName: String,
        owner: String,
        active: String,
        posting: String,
        memo: String
    ): RegisterResult

    fun resendSmsCode(phone: String): ResultOk
}