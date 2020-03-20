package io.golos.data.repositories

import io.golos.commun4j.Commun4j
import io.golos.commun4j.services.model.*
import io.golos.commun4j.sharedmodel.CyberName
import io.golos.data.mappers.mapToAuthResultDomain
import io.golos.data.mappers.mapToBCProfileDomain
import io.golos.data.network_state.NetworkStateChecker
import io.golos.domain.DispatchersProvider
import io.golos.domain.dto.AuthResultDomain
import io.golos.domain.dto.UserIdDomain
import io.golos.domain.dto.bc_profile.BCProfileDomain
import io.golos.domain.repositories.AuthRepository
import javax.inject.Inject

class AuthRepositoryImpl
@Inject constructor(
    dispatchersProvider: DispatchersProvider,
    networkStateChecker: NetworkStateChecker,
    private val commun4j: Commun4j
) : RepositoryBase(dispatchersProvider, networkStateChecker),
    AuthRepository {

    override suspend fun auth(userName: String, secret: String, signedSecret: String): AuthResultDomain =
        apiCall { commun4j.authWithSecret(userName, secret, signedSecret) }.mapToAuthResultDomain()

    override suspend fun getAuthSecret(): String = apiCall { commun4j.getAuthSecret() }.secret

    override suspend fun getUserBlockChainProfile(userId: UserIdDomain): BCProfileDomain =
        apiCallChain { commun4j.getUserAccount(CyberName(userId.userId)) }.mapToBCProfileDomain()

    override suspend fun writeUserToBlockChain(phone: String, userId: String, userName: String, owner: String, active: String) {
        apiCall { commun4j.writeUserToBlockChain(
            phone = phone,
            identity = null,
            userName = userName,
            userId = userId,
            owner = owner,
            active = active) }
    }

    override suspend fun getRegistrationState(phone: String): UserRegistrationStateResult =
        apiCall { commun4j.getRegistrationState(phone = phone, identity = null) }

    override suspend fun firstUserRegistrationStep(phone: String, testingPass: String?): FirstRegistrationStepResult =
        apiCall { commun4j.firstUserRegistrationStep("", phone, testingPass) }

    override suspend fun verifyPhoneForUserRegistration(phone: String, code: Int): VerifyStepResult =
        apiCall { commun4j.verifyPhoneForUserRegistration(phone, code) }

    override suspend fun setVerifiedUserName(user: String, phone: String): SetUserNameStepResult =
        apiCall { commun4j.setVerifiedUserName(user = user, phone = phone, identity = null) }

    override suspend fun resendSmsCode(phone: String): ResultOk =
        apiCall { commun4j.resendSmsCode(phone) }
}