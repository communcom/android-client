package io.golos.domain.repositories

import io.golos.commun4j.services.model.*
import io.golos.domain.dto.AuthResultDomain
import io.golos.domain.dto.UserIdDomain
import io.golos.domain.dto.bc_profile.BCProfileDomain

interface AuthRepository {
    suspend fun auth(userName: String, secret: String, signedSecret: String): AuthResultDomain

    suspend fun getAuthSecret(): String

    suspend fun getUserBlockChainProfile(userId: UserIdDomain): BCProfileDomain

    suspend fun writeUserToBlockChain(
        phone: String?,
        identity: String?,
        userId: String,
        userName: String,
        owner: String,
        active: String)

    suspend fun getRegistrationState(phone: String?, identity: String?): UserRegistrationStateResult

    suspend fun firstUserRegistrationStep(
        phone: String,
        testingPass: String?
    ): FirstRegistrationStepResult

    suspend fun verifyPhoneForUserRegistration(phone: String, code: Int): VerifyStepResult

    suspend fun setVerifiedUserName(user: String, phone: String?, identity: String?): SetUserNameStepResult

    suspend fun resendSmsCode(phone: String)
}