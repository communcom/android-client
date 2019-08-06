package io.golos.data.repositories

import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.golos.data.api.PushNotificationsApi
import io.golos.data.errors.CyberToAppErrorMapper
import io.golos.domain.*
import io.golos.domain.entities.PushNotificationsStateEntity
import io.golos.domain.requestmodel.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

class PushNotificationsRepository
@Inject
constructor(
    private val api: PushNotificationsApi,
    private val deviceIdProvider: DeviceIdProvider,
    private val fcmTokenProvider: FcmTokenProvider,
    private val toAppErrorMapper: CyberToAppErrorMapper,
    private val logger: Logger,
    dispatchersProvider: DispatchersProvider
) : Repository<PushNotificationsStateEntity, PushNotificationsStateUpdateRequest> {

    private val repositoryScope = CoroutineScope(dispatchersProvider.calculationskDispatcher + SupervisorJob())
    private var lastUpdateJob: Job? = null
    private val updatingStates = MutableLiveData<Map<Identifiable.Id, QueryResult<PushNotificationsStateUpdateRequest>>>()

    override val allDataRequest
            get() = throw UnsupportedOperationException()

    override fun getAsLiveData(params: PushNotificationsStateUpdateRequest): LiveData<PushNotificationsStateEntity> =
        throw UnsupportedOperationException()

    @MainThread
    override fun makeAction(params: PushNotificationsStateUpdateRequest) {
        updatingStates.value = updatingStates.value.orEmpty() +
                (params.id to QueryResult.Loading(params))
        repositoryScope.launch {
            try {
                when (params) {
                    is PushNotificationsSubscribeRequest -> api.subscribeOnMobilePushNotifications(deviceIdProvider.provide(), fcmTokenProvider.provide())
                    is PushNotificationsUnsubscribeRequest -> api.unSubscribeOnNotifications(params.userId, deviceIdProvider.provide())
                }

                updatingStates.postValue(updatingStates.value.orEmpty() +
                        (params.id to QueryResult.Success(params)))

            } catch (e: Exception) {
                updatingStates.postValue(updatingStates.value.orEmpty() +
                        (params.id to QueryResult.Error(toAppErrorMapper.mapIfNeeded(e), params)))
                logger.log(e)
            }
        }.let {
            lastUpdateJob?.cancel()
            lastUpdateJob = it
        }
    }

    override val updateStates: LiveData<Map<Identifiable.Id, QueryResult<PushNotificationsStateUpdateRequest>>> = updatingStates

}