package io.golos.data.api

import io.golos.cyber4j.model.CyberName
import io.golos.cyber4j.services.model.FirstRegistrationStepResult
import io.golos.cyber4j.services.model.ResultOk
import io.golos.cyber4j.services.model.UserRegistrationStateResult

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-04-11.
 */
interface RegistrationApi {
    fun getRegistrationState(phone: String): UserRegistrationStateResult

    fun firstUserRegistrationStep(
        phone: String,
        testingPass: String?
    ): FirstRegistrationStepResult

    fun verifyPhoneForUserRegistration(phone: String, code: Int): ResultOk

    fun setVerifiedUserName(user: CyberName, phone: String): ResultOk

    fun writeUserToBlockChain(
        userName: CyberName,
        owner: String,
        active: String,
        posting: String,
        memo: String
    ): ResultOk

    fun resendSmsCode(phone: String): ResultOk
}