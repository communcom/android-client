package io.golos.data.api.registration

import io.golos.commun4j.services.model.*

interface RegistrationApi {
    fun getRegistrationState(phone: String): UserRegistrationStateResult

    fun firstUserRegistrationStep(
        phone: String,
        testingPass: String?
    ): FirstRegistrationStepResult

    fun verifyPhoneForUserRegistration(phone: String, code: Int): VerifyStepResult

    fun setVerifiedUserName(user: String, phone: String): SetUserNameStepResult

    fun writeUserToBlockChain(
        phone: String,
        userId: String,
        userName: String,
        owner: String,
        active: String
    ): WriteToBlockChainStepResult

    fun resendSmsCode(phone: String): ResultOk
}