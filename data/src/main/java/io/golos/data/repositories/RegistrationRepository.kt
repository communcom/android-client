package io.golos.data.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.golos.commun4j.services.model.UserRegistrationState
import io.golos.commun4j.services.model.UserRegistrationStateResult
import io.golos.data.api.registration.RegistrationApi
import io.golos.data.errors.CyberToAppErrorMapper
import io.golos.domain.DispatchersProvider
import io.golos.domain.repositories.Repository
import io.golos.domain.dependency_injection.scopes.ApplicationScope
import io.golos.domain.entities.UserRegistrationStateEntity
import io.golos.domain.mappers.UserRegistrationStateEntityMapper
import io.golos.domain.mappers.UserRegistrationStateRelatedData
import io.golos.domain.requestmodel.*
import kotlinx.coroutines.*
import timber.log.Timber
import java.util.*
import javax.inject.Inject

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-04-11.
 */
@ApplicationScope
class RegistrationRepository
@Inject
constructor(
    private val registrationApi: RegistrationApi,
    private val dispatchersProvider: DispatchersProvider,
    private val toAppErrorMapper: CyberToAppErrorMapper
) : Repository<UserRegistrationStateEntity, RegistrationStepRequest> {

    private val repositoryScope = CoroutineScope(dispatchersProvider.uiDispatcher + SupervisorJob())

    private val registrationRequestsLiveData =
        MutableLiveData<Map<Identifiable.Id, QueryResult<RegistrationStepRequest>>>()

    private val registerJobsMap = Collections.synchronizedMap(HashMap<Identifiable.Id, Job>())

    private val registrationStates =
        Collections.synchronizedMap(HashMap<Identifiable.Id, MutableLiveData<UserRegistrationStateEntity>>())

    private val allMyDataRequest = ResendSmsVerificationCode("9999999999999999999999")

    override val allDataRequest: RegistrationStepRequest
        get() = allMyDataRequest

    override fun getAsLiveData(params: RegistrationStepRequest): LiveData<UserRegistrationStateEntity> {
        return registrationStates.getOrPut(params.id) {
            MutableLiveData()
        }
    }

    override fun makeAction(params: RegistrationStepRequest) {
        repositoryScope.launch {

            registrationRequestsLiveData.value =
                registrationRequestsLiveData.value.orEmpty() + (params.id to QueryResult.Loading(params))

            try {
                (getAsLiveData(params) as MutableLiveData).value =
                    withContext(dispatchersProvider.calculationsDispatcher) {
                        val actionResult =
                            @Suppress("IMPLICIT_CAST_TO_ANY")
                            when (params) {
                                is GetUserRegistrationStepRequest -> null
                                is SendSmsForVerificationRequest ->
                                    registrationApi.firstUserRegistrationStep(params.phone, params.testCode)
                                is SendVerificationCodeRequest ->
                                    registrationApi.verifyPhoneForUserRegistration(params.phone, params.code)

                                is SetUserNameRequest ->
                                    registrationApi.setVerifiedUserName(params.userName, params.phone)

                                is SetUserKeysRequest -> {
                                    registrationApi.writeUserToBlockChain(
                                        params.userName,
                                        params.ownerPublicKey,
                                        params.activePublicKey,
                                        params.postingPublicKey,
                                        params.memoPublicKey
                                    )
                                }
                                is ResendSmsVerificationCode ->
                                    registrationApi.resendSmsCode(params.phone)
                            }

                        val regState =
                            if (params !is SetUserKeysRequest) registrationApi.getRegistrationState(params.phone)
                            else UserRegistrationStateResult(UserRegistrationState.REGISTERED, params.userName)

                        UserRegistrationStateEntityMapper.map(
                            UserRegistrationStateRelatedData(
                                actionResult,
                                params,
                                regState
                            )
                        )
                    }
                registrationRequestsLiveData.value =
                    registrationRequestsLiveData.value.orEmpty() + (params.id to QueryResult.Success(params))

            } catch (e: Exception) {
                Timber.e(e)
                registrationRequestsLiveData.value =
                    registrationRequestsLiveData.value.orEmpty() + (params.id to QueryResult.Error(
                        toAppErrorMapper.mapIfNeeded(e),
                        params
                    ))
            }

        }.let { job ->
            registerJobsMap[params.id]?.cancel()
            registerJobsMap[params.id] = job
        }
    }

    override val updateStates: LiveData<Map<Identifiable.Id, QueryResult<RegistrationStepRequest>>> =
        registrationRequestsLiveData
}