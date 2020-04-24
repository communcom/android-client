package io.golos.data.repositories

import io.golos.commun4j.Commun4j
import io.golos.commun4j.services.model.*
import io.golos.commun4j.sharedmodel.CyberName
import io.golos.data.mappers.mapToAuthResultDomain
import io.golos.data.mappers.mapToBCProfileDomain
import io.golos.data.repositories.network_call.NetworkCallProxy
import io.golos.domain.dto.AuthResultDomain
import io.golos.domain.dto.UserIdDomain
import io.golos.domain.dto.bc_profile.BCProfileDomain
import io.golos.domain.repositories.AuthRepository
import timber.log.Timber
import javax.inject.Inject

class AuthRepositoryImpl
@Inject constructor(
    private val callProxy: NetworkCallProxy,
    private val commun4j: Commun4j
) : AuthRepository {

    override suspend fun auth(userName: String, secret: String, signedSecret: String): AuthResultDomain =
        callProxy.call {
            Timber.tag("NET_SOCKET").d("AuthRepositoryImpl::auth(userName = $userName, secret = $secret, signedSecret = $signedSecret)")
            commun4j.authWithSecret(userName, secret, signedSecret)
        }.mapToAuthResultDomain()

    override suspend fun getAuthSecret(): String = callProxy.call {
        Timber.tag("NET_SOCKET").d("AuthRepositoryImpl::getAuthSecret()")
        commun4j.getAuthSecret()
    }.secret

    override suspend fun getUserBlockChainProfile(userId: UserIdDomain): BCProfileDomain =
        callProxy.callBC {
            Timber.tag("NET_SOCKET").d("AuthRepositoryImpl::getUserBlockChainProfile(userId = $userId)")
            commun4j.getUserAccount(CyberName(userId.userId))
        }.mapToBCProfileDomain()

    override suspend fun writeUserToBlockChain(
        phone: String?,
        identity: String?,
        email: String?,
        userId: String,
        userName: String,
        owner: String,
        active: String): Boolean =
        callProxy.call {
            Timber.tag("NET_SOCKET").d("AuthRepositoryImpl::writeUserToBlockChain(phone = $phone, identity = $identity, userId = $userId, userName = $userName, owner = $owner, active = $active)")
            commun4j.writeUserToBlockChain(
                phone = phone,
                identity = identity,
                email = email,
                userName = userName,
                userId = userId,
                owner = owner,
                active = active) }.currentState == "registered"

    override suspend fun getRegistrationState(phone: String?, identity: String?): UserRegistrationStateResult =
        callProxy.call {
            Timber.tag("NET_SOCKET").d("AuthRepositoryImpl::getRegistrationState(phone = $phone, identity = $identity)")
            commun4j.getRegistrationState(phone = phone, identity = identity)
        }

    override suspend fun firstPhoneUserRegistrationStep(captcha: String, phone: String): FirstRegistrationStepResult =
        callProxy.call {
            Timber.tag("NET_SOCKET").d("AuthRepositoryImpl::firstUserRegistrationStep(phone = $phone)")
            commun4j.firstUserRegistrationStep(captcha = captcha, phone = phone, testingPass = null, captchaType = "android")
        }

    override suspend fun firstEmailUserRegistrationStep(captcha: String, email: String): FirstRegistrationStepEmailResult =
        callProxy.call {
            Timber.tag("NET_SOCKET").d("AuthRepositoryImpl::firstUserRegistrationStepEmail(email = $email)")
            commun4j.firstUserRegistrationStepEmail(captcha = captcha, email = email, testingPass = null, captchaType = "android")
        }

    override suspend fun verifyPhoneForUserRegistration(phone: String, code: Int): VerifyStepResult =
        callProxy.call {
            Timber.tag("NET_SOCKET").d("AuthRepositoryImpl::verifyPhoneForUserRegistration(phone = $phone, code = $code)")
            commun4j.verifyPhoneForUserRegistration(phone, code)
        }

    override suspend fun verifyEmailForUserRegistration(email: String, code: String): VerifyStepResult =
        callProxy.call {
            Timber.tag("NET_SOCKET").d("AuthRepositoryImpl::verifyEmailForUserRegistration(email = $email, code = $code)")
            commun4j.verifyEmailForUserRegistration(email, code)
        }

    override suspend fun setVerifiedUserName(user: String, phone: String?, identity: String?, email: String?): SetUserNameStepResult =
        callProxy.call {
            Timber.tag("NET_SOCKET").d("AuthRepositoryImpl::setVerifiedUserName(user = $user, phone = $phone, identity = $identity, email = $email)")
            commun4j.setVerifiedUserName(user = user, phone = phone, identity = identity, email = email)
        }

    override suspend fun resendSmsCode(phone: String) {
        callProxy.call {
            Timber.tag("NET_SOCKET").d("AuthRepositoryImpl::resendSmsCode(phone = $phone)")
            commun4j.resendSmsCode(phone)
        }
    }

    override suspend fun resendEmail(email: String) {
        callProxy.call {
            Timber.tag("NET_SOCKET").d("AuthRepositoryImpl::resendEmail(email = $email)")
            commun4j.resendEmail(email)
        }
    }
}