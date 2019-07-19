package io.golos.domain.interactors.reg

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import io.golos.cyber4j.model.AuthType
import io.golos.cyber4j.utils.AuthUtils
import io.golos.domain.DispatchersProvider
import io.golos.domain.Repository
import io.golos.domain.UserKeyStore
import io.golos.domain.entities.*
import io.golos.domain.interactors.UseCase
import io.golos.domain.interactors.model.*
import io.golos.domain.map
import io.golos.domain.requestmodel.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.util.*

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-04-11.
 */
class SignUpUseCase(
    val inTestMode: Boolean,
    private val registrationRepository: Repository<UserRegistrationStateEntity, RegistrationStepRequest>,
    private val authRepository: Repository<AuthState, AuthRequest>,
    dispatchersProvider: DispatchersProvider,
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
                    lastRegisteredUser.value = lastRequestLocal.userName

                    if (authRepository.getAsLiveData(authRepository.allDataRequest).value?.isUserLoggedIn != true) {
                        authRepository.makeAction(
                            AuthRequest(
                                CyberUser(lastRequestLocal.userName),
                                userKeyStore.getKey(UserKeyType.ACTIVE)
                            )
                        )
                    }
                }
                RegisteredUserModel(it.userName)
            }
            is UnWrittenToBlockChainUser -> UnWrittenToBlockChainUserModel()
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
                                is GetUserRegistrationStepRequest -> GetUserRegistrationStepRequestModel(originalQuery.phone)
                                is SendSmsForVerificationRequest -> SendSmsForVerificationRequestModel(originalQuery.phone)
                                is SendVerificationCodeRequest -> SendVerificationCodeRequestModel(
                                    originalQuery.phone,
                                    originalQuery.code
                                )
                                is SetUserNameRequest -> SetUserNameRequestModel(
                                    originalQuery.phone,
                                    originalQuery.userName
                                )
                                is SetUserKeysRequest -> WriteUserToBlockChainRequestModel(
                                    originalQuery.phone,
                                    originalQuery.userName
                                )
                                is ResendSmsVerificationCode -> ResendSmsVerificationCodeModel(originalQuery.phone)
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
                GetUserRegistrationStepRequest(lastRequestLocal.phone)
            )
        )
    }

    fun makeRegistrationStep(param: NextRegistrationStepRequestModel) {

        val lastRequestLocal = lastRequest
        if (lastRequestLocal?.phone != param.phone) {

            if (lastRequestLocal != null) mediator.removeSource(
                registrationRepository.getAsLiveData(
                    GetUserRegistrationStepRequest(lastRequestLocal.phone)
                )
            )
            mediator.addSource(
                registrationRepository.getAsLiveData(GetUserRegistrationStepRequest(param.phone)),
                registrationStepsObserver
            )
        }

        lastRequest = param
        registrationRepository.makeAction(
            when (param) {
                is GetUserRegistrationStepRequestModel -> GetUserRegistrationStepRequest(param.phone)
                is SendSmsForVerificationRequestModel -> SendSmsForVerificationRequest(
                    param.phone,
                    if (inTestMode) testPassProvider.provide() else null
                )
                is SendVerificationCodeRequestModel -> SendVerificationCodeRequest(param.phone, param.code)
                is SetUserNameRequestModel -> SetUserNameRequest(param.phone, param.userName)
                is WriteUserToBlockChainRequestModel -> {
                    // Keys are generated and sent to server (public parts only)
                    val userKeys = userKeyStore.createKeys(param.userName)
                    SetUserKeysRequest(
                        param.phone,
                        userKeys.userName,
                        userKeys.masterPassword,
                        userKeys.ownerPublicKey,
                        userKeys.ownerPrivateKey,
                        userKeys.activePublicKey,
                        userKeys.activePrivateKey,
                        userKeys.postingPublicKey,
                        userKeys.postingPrivateKey,
                        userKeys.memoPublicKey,
                        userKeys.memoPrivateKey
                    )
                }
                is ResendSmsVerificationCodeModel -> ResendSmsVerificationCode(param.phone)
            }
        )
    }
}