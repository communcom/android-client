package io.golos.data.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.golos.data.api.PushNotificationsApi
import io.golos.data.errors.CyberToAppErrorMapper
import io.golos.domain.*
import io.golos.domain.entities.PushNotificationsStateEntity
import io.golos.domain.requestmodel.Identifiable
import io.golos.domain.requestmodel.PushNotificationsRequest
import io.golos.domain.requestmodel.QueryResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class PushNotificationsRepository(
    private val api: PushNotificationsApi,
    private val deviceIdProvider: DeviceIdProvider,
    private val fcmTokenProvider: FcmTokenProvider,
    private val toAppErrorMapper: CyberToAppErrorMapper,
    private val logger: Logger,
    dispatchersProvider: DispatchersProvider
) : Repository<PushNotificationsStateEntity, PushNotificationsRequest> {

    private val repositoryScope = CoroutineScope(dispatchersProvider.workDispatcher + SupervisorJob())
    private var lastUpdateJob: Job? = null
    private val updatingStates = MutableLiveData<Map<Identifiable.Id, QueryResult<PushNotificationsRequest>>>()

    override val allDataRequest = throw UnsupportedOperationException()

    override fun getAsLiveData(params: PushNotificationsRequest): LiveData<PushNotificationsStateEntity> =
        throw UnsupportedOperationException()

    override fun makeAction(params: PushNotificationsRequest) {
        repositoryScope.launch {
            try {
                if (params.toEnable)
                    api.subscribeOnMobilePushNotifications(deviceIdProvider.provide(), fcmTokenProvider.provide())
                else api.unSubscribeOnNotifications(deviceIdProvider.provide(), fcmTokenProvider.provide())

                updatingStates.postValue(updatingStates.value.orEmpty() +
                        (params.id to QueryResult.Success(params)))

            } catch (e: Exception) {
                updatingStates.postValue(updatingStates.value.orEmpty() +
                        (params.id to QueryResult.Error(toAppErrorMapper.mapIfNeeded(e), params)))
                logger(e)
            }
        }.let {
            lastUpdateJob?.cancel()
            lastUpdateJob = it
        }
    }

    override val updateStates: LiveData<Map<Identifiable.Id, QueryResult<PushNotificationsRequest>>> = updatingStates

}