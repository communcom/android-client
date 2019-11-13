package io.golos.data.api.registration

import io.golos.commun4j.Commun4j
import io.golos.commun4j.services.model.*
import io.golos.data.api.Commun4jApiBase
import io.golos.domain.repositories.CurrentUserRepositoryRead
import javax.inject.Inject

class RegistrationApiImpl
@Inject
constructor(
    commun4j: Commun4j,
    currentUserRepository: CurrentUserRepositoryRead
) : Commun4jApiBase(commun4j, currentUserRepository), RegistrationApi {

    override fun getRegistrationState(phone: String): UserRegistrationStateResult =
        commun4j.getRegistrationState(phone).getOrThrow()

    override fun firstUserRegistrationStep(phone: String, testingPass: String?): FirstRegistrationStepResult =
        commun4j.firstUserRegistrationStep("", phone, testingPass).getOrThrow()

    override fun verifyPhoneForUserRegistration(phone: String, code: Int): VerifyStepResult =
        commun4j.verifyPhoneForUserRegistration(phone, code).getOrThrow()

    override fun setVerifiedUserName(user: String, phone: String): SetUserNameStepResult = commun4j.setVerifiedUserName(user, phone).getOrThrow()

    override fun writeUserToBlockChain(
        phone: String,
        userId: String,
        userName: String,
        owner: String,
        active: String
    ): WriteToBlockChainStepResult {

        return commun4j.writeUserToBlockChain(phone, userId, userName, owner, active).getOrThrow()
    }


    override fun resendSmsCode(phone: String): ResultOk = commun4j.resendSmsCode(phone).getOrThrow()
}