package io.golos.domain.interactors.reg

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import io.golos.cyber4j.model.AuthType
import io.golos.cyber4j.model.CyberName
import io.golos.cyber4j.utils.AuthUtils
import io.golos.domain.DispatchersProvider
import io.golos.domain.Repository
import io.golos.domain.entities.*
import io.golos.domain.interactors.UseCase
import io.golos.domain.interactors.model.*
import io.golos.domain.map
import io.golos.domain.model.*
import java.util.*

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-04-11.
 */
class SignOnUseCase(
    val inTestMode: Boolean,
    private val registrationRepository: Repository<UserRegistrationStateEntity, RegistrationStepRequest>,
    private val dispatchersProvider: DispatchersProvider,
    private val testPassProvider: TestPassProvider
) : UseCase<UserRegistrationStateModel> {

    private val observer = Observer<Any> {}
    private val mediator = MediatorLiveData<Any>()

    private val lastRegisteredUser = MutableLiveData<GeneratedUserKeys>()

    private var lastGeneratedKeys: GeneratedUserKeys? = null

    private val currentUserRegistrationLiveData = MutableLiveData<UserRegistrationStateModel>()
    private val updatingState = MutableLiveData<QueryResult<NextRegistrationStepRequestModel>>()

    override val getAsLiveData: LiveData<UserRegistrationStateModel> =
        currentUserRegistrationLiveData

    val getUpdatingState: LiveData<QueryResult<NextRegistrationStepRequestModel>> = updatingState

    val getLastRegisteredUser: LiveData<GeneratedUserKeys> = lastRegisteredUser

    private var lastRequest: NextRegistrationStepRequestModel? = null
    private val registrationStepsObserver = Observer<UserRegistrationStateEntity> {
        it ?: return@Observer
        currentUserRegistrationLiveData.value = when (it) {
            is UnregisteredUser -> UnregisteredUserModel()
            is UnverifiedUser -> UnverifiedUserModel(it.nextSmsVerification, it.smsCode)
            is VerifiedUserWithoutUserName -> VerifiedUserWithoutUserNameModel()
            is RegisteredUser -> {
                if (it.userName == lastGeneratedKeys?.userName) {
                    lastGeneratedKeys?.let { keys ->
                        lastRegisteredUser.value = keys//todo move this state fiddling to repository
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

            println(it)

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
                    val userKeys = generateUserKeys(param.userName)
                    SetUserKeysRequest(
                        param.phone,
                        userKeys.userName,
                        userKeys.ownerKey,
                        userKeys.activeKey,
                        userKeys.postingKey,
                        userKeys.memoKey
                    )
                }
                is ResendSmsVerificationCodeModel -> ResendSmsVerificationCode(param.phone)
            }
        )
    }

    private fun generateUserKeys(forUser: CyberName): GeneratedUserKeys {
        val masterPassword = (UUID.randomUUID().toString() + UUID.randomUUID().toString()).replace("-", "")
        val keys = AuthUtils.generatePublicWiFs(
            forUser.name,
            masterPassword,
            AuthType.values()
        )
        val userKeys = GeneratedUserKeys(
            forUser,
            masterPassword,
            keys[AuthType.OWNER]!!,
            keys[AuthType.ACTIVE]!!,
            keys[AuthType.POSTING]!!,
            keys[AuthType.MEMO]!!
        )
        lastGeneratedKeys = userKeys
        return userKeys
    }
}