package io.golos.domain.use_cases.reg

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import io.golos.domain.DispatchersProvider
import io.golos.domain.repositories.Repository
import io.golos.domain.UserKeyStore
import io.golos.domain.api.AuthApi
import io.golos.domain.dto.*
import io.golos.domain.use_cases.UseCase
import io.golos.domain.use_cases.model.*
import io.golos.domain.extensions.map
import io.golos.domain.repositories.AuthStateRepository
import io.golos.domain.repositories.RegistrationRepository
import io.golos.domain.requestmodel.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-04-11.
 */
class SignUpUseCase
@Inject
constructor(
    private val registrationRepository: RegistrationRepository,
    private val authRepository: AuthStateRepository,
    private val authApi: AuthApi,
    private val dispatchersProvider: DispatchersProvider,
    private val testPassProvider: TestPassProvider,
    private val userKeyStore: UserKeyStore
) : UseCase<UserRegistrationStateModel> {

    private val observer = Observer<Any> {}
    private val mediator = MediatorLiveData<Any>()

    private val lastRegisteredUser = MutableLiveData<String>()

    private val currentUserRegistrationLiveData = MutableLiveData<UserRegistrationStateModel>()
    private val updatingState = MutableLiveData<QueryResult<NextRegistrationStepRequestModel>>()

    private val useCaseScope = CoroutineScope(dispatchersProvider.uiDispatcher + SupervisorJob())

    override val getAsLiveData: LiveData<UserRegistrationStateModel> =
        currentUserRegistrationLiveData

    val getUpdatingState: LiveData<QueryResult<NextRegistrationStepRequestModel>> = updatingState

    val getLastRegisteredUser: LiveData<String> = lastRegisteredUser

    var userName: String = ""

    private var lastRequest: NextRegistrationStepRequestModel? = null

    private val registrationStepsObserver = Observer<UserRegistrationStateEntity> {
        it ?: return@Observer

        currentUserRegistrationLiveData.value = when (it) {
            is UnregisteredUser -> UnregisteredUserModel()
            is UnverifiedUser -> UnverifiedUserModel(it.nextSmsVerification, it.smsCode)
            is VerifiedUserWithoutUserName -> VerifiedUserWithoutUserNameModel()
            is RegisteredUser -> {
                val lastRequestLocal = lastRequest as? WriteUserToBlockChainRequestModel
                if (it.userName == lastRequestLocal?.userName &&
                    it.masterPassword != null
                ) {
                    // Keys were generated again and used for authentication
                    lastRegisteredUser.value = lastRequestLocal!!.userName

                    if (authRepository.getAsLiveData(authRepository.allDataRequest).value?.isUserLoggedIn != true) {
                        authRepository.makeAction(
                            AuthRequest(
                                lastRequestLocal.userName,
                                CyberUser(lastRequestLocal.userId),
                                userKeyStore.getKey(UserKeyType.ACTIVE),
                                AuthType.SIGN_UP
                            )
                        )
                    }
                }
                RegisteredUserModel(it.userName)
            }
            is UnWrittenToBlockChainUser -> UnWrittenToBlockChainUserModel(it.userName, it.userId)
        }
    }


    override fun subscribe() {
        super.subscribe()
        mediator.observeForever(observer)
        mediator.addSource(registrationRepository.updateStates) {
            useCaseScope.launch {
                val map = it
                if (map.isNullOrEmpty()) updatingState.value = null
                else {
                    val myState = map
                        .filterKeys { (it as RegistrationStepRequest.Id)._phone == lastRequest?.phone }
                        .values
                        .firstOrNull()
                    if (myState == null) {
                        updatingState.value = null
                    } else {
                        val originalQuery = myState.originalQuery
                        updatingState.value = myState.map(
                            when (originalQuery) {
                                is GetUserRegistrationStepRequest -> GetUserRegistrationStepRequestModel(originalQuery.phone, originalQuery.identity)
                                is SendSmsForVerificationRequest -> SendSmsForVerificationRequestModel(originalQuery.captcha, originalQuery.phone, originalQuery.identity)
                                is SendVerificationCodeRequest -> SendVerificationCodeRequestModel(
                                    originalQuery.phone,
                                    originalQuery.identity,
                                    originalQuery.code
                                )
                                is SetUserNameRequest -> SetUserNameRequestModel(
                                    originalQuery.phone,
                                    originalQuery.identity,
                                    originalQuery.userName
                                )
                                is SetUserKeysRequest -> WriteUserToBlockChainRequestModel(
                                    originalQuery.phone,
                                    originalQuery.identity,
                                    originalQuery.userName,
                                    originalQuery.userId
                                )
                                is ResendSmsVerificationCode -> ResendSmsVerificationCodeModel(originalQuery.phone, originalQuery.identity)
                            }
                        )
                    }
                }

            }
        }

    }

    override fun unsubscribe() {
        super.unsubscribe()
        mediator.removeObserver(observer)
        mediator.removeSource(registrationRepository.updateStates)

        val lastRequestLocal = lastRequest
        if (lastRequestLocal != null) mediator.removeSource(
            registrationRepository.getAsLiveData(
                GetUserRegistrationStepRequest(lastRequestLocal.phone, lastRequestLocal.identity)
            )
        )
    }

    suspend fun makeRegistrationStep(param: NextRegistrationStepRequestModel) {

        val lastRequestLocal = lastRequest
        if (lastRequestLocal?.phone != param.phone || (lastRequestLocal?.identity != param.identity)) {

            if (lastRequestLocal != null) mediator.removeSource(
                registrationRepository.getAsLiveData(
                    GetUserRegistrationStepRequest(lastRequestLocal.phone, lastRequestLocal.identity)
                )
            )
            mediator.addSource(
                registrationRepository.getAsLiveData(GetUserRegistrationStepRequest(param.phone, param.identity)),
                registrationStepsObserver
            )
        }

        lastRequest = param
        registrationRepository.makeAction(
            when (param) {
                is GetUserRegistrationStepRequestModel -> GetUserRegistrationStepRequest(param.phone, param.identity)
                is SendSmsForVerificationRequestModel -> SendSmsForVerificationRequest(
                    param.captcha,
                    param.phone,
                    param.identity,
                    testPassProvider.provide()
                )
                is SendVerificationCodeRequestModel -> SendVerificationCodeRequest(param.phone, param.identity, param.code)
                is SetUserNameRequestModel -> SetUserNameRequest(param.phone, param.identity, param.userName)
                is WriteUserToBlockChainRequestModel -> {
                    val userId = param.userId
                    // Keys are generated and sent to server (public parts only)
//                    val userKeys = userKeyStore.createKeys(UserIdDomain(userId), param.userName)
//                    SetUserKeysRequest(
//                        param.phone,
//                        userId,
//                        userKeys.userName,
//                        userKeys.masterPassword,
//                        userKeys.ownerPublicKey,
//                        userKeys.ownerPrivateKey,
//                        userKeys.activePublicKey,
//                        userKeys.activePrivateKey,
//                        userKeys.postingPublicKey,
//                        userKeys.postingPrivateKey,
//                        userKeys.memoPublicKey,
//                        userKeys.memoPrivateKey
//                    )
                    SetUserKeysRequest(
                        param.phone,
                        param.identity,
                        userId,
                        param.userName,
                        "",
                        "",
                        "",
                        "",
                        "",
                        "",
                        "",
                        "",
                        ""
                    )

                }
                is ResendSmsVerificationCodeModel -> ResendSmsVerificationCode(param.phone, param.identity)
            }
        )
    }
}