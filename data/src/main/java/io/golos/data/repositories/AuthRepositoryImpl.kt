package io.golos.data.repositories

import io.golos.commun4j.Commun4j
import io.golos.commun4j.services.model.FirstRegistrationStepResult
import io.golos.commun4j.services.model.SetUserNameStepResult
import io.golos.commun4j.services.model.UserRegistrationStateResult
import io.golos.commun4j.services.model.VerifyStepResult
import io.golos.commun4j.sharedmodel.CyberName
import io.golos.data.mappers.mapToAuthResultDomain
import io.golos.data.mappers.mapToBCProfileDomain
import io.golos.data.network_state.NetworkStateChecker
import io.golos.domain.DispatchersProvider
import io.golos.domain.dto.AuthResultDomain
import io.golos.domain.dto.UserIdDomain
import io.golos.domain.dto.bc_profile.BCProfileDomain
import io.golos.domain.repositories.AuthRepository
import timber.log.Timber
import javax.inject.Inject

class AuthRepositoryImpl
@Inject constructor(
    dispatchersProvider: DispatchersProvider,
    networkStateChecker: NetworkStateChecker,
    private val commun4j: Commun4j
) : RepositoryBase(dispatchersProvider, networkStateChecker),
    AuthRepository {

    override suspend fun auth(userName: String, secret: String, signedSecret: String): AuthResultDomain =
        apiCall {
            Timber.tag("NET_SOCKET").d("AuthRepositoryImpl::auth(userName = $userName, secret = $secret, signedSecret = $signedSecret)")
            commun4j.authWithSecret(userName, secret, signedSecret)
        }.mapToAuthResultDomain()

    override suspend fun getAuthSecret(): String = apiCall {
        Timber.tag("NET_SOCKET").d("AuthRepositoryImpl::getAuthSecret()")
        commun4j.getAuthSecret()
    }.secret

    override suspend fun getUserBlockChainProfile(userId: UserIdDomain): BCProfileDomain =
        apiCallChain {
            Timber.tag("NET_SOCKET").d("AuthRepositoryImpl::getUserBlockChainProfile(userId = $userId)")
            commun4j.getUserAccount(CyberName(userId.userId))
        }.mapToBCProfileDomain()

    override suspend fun writeUserToBlockChain(
        phone: String?,
        identity: String?,
        userId: String,
        userName: String,
        owner: String,
        active: String) {
        apiCall {
            Timber.tag("NET_SOCKET").d("AuthRepositoryImpl::writeUserToBlockChain(phone = $phone, identity = $identity, userId = $userId, userName = $userName, owner = $owner, active = $active)")
            commun4j.writeUserToBlockChain(
                phone = phone,
                identity = identity,
                userName = userName,
                userId = userId,
                owner = owner,
                active = active,
                email = null) }
    }

    override suspend fun getRegistrationState(phone: String?, identity: String?): UserRegistrationStateResult =
        apiCall {
            Timber.tag("NET_SOCKET").d("AuthRepositoryImpl::getRegistrationState(phone = $phone, identity = $identity)")
            commun4j.getRegistrationState(phone = phone, identity = identity)
        }

    override suspend fun firstUserRegistrationStep(captcha: String?, phone: String, testingPass: String?): FirstRegistrationStepResult =
        apiCall {
            Timber.tag("NET_SOCKET").d("AuthRepositoryImpl::firstUserRegistrationStep(phone = $phone, testingPass = $testingPass)")
            commun4j.firstUserRegistrationStep(captcha, phone, testingPass, "android")
        }

    override suspend fun verifyPhoneForUserRegistration(phone: String, code: Int): VerifyStepResult =
        apiCall {
            Timber.tag("NET_SOCKET").d("AuthRepositoryImpl::verifyPhoneForUserRegistration(phone = $phone, code = $code)")
            commun4j.verifyPhoneForUserRegistration(phone, code)
        }

    override suspend fun setVerifiedUserName(user: String, phone: String?, identity: String?): SetUserNameStepResult =
        apiCall {
            Timber.tag("NET_SOCKET").d("AuthRepositoryImpl::setVerifiedUserName(user = $user, phone = $phone, identity = $identity)")
            commun4j.setVerifiedUserName(user = user, phone = phone, identity = identity)
        }

    override suspend fun resendSmsCode(phone: String) {
        apiCall {
            Timber.tag("NET_SOCKET").d("AuthRepositoryImpl::resendSmsCode(phone = $phone)")
            commun4j.resendSmsCode(phone)
        }
    }
}