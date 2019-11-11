package io.golos.domain.use_cases.notifs.push

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import io.golos.domain.KeyValueStorageFacade
import io.golos.domain.repositories.Repository
import io.golos.domain.dto.PushNotificationsStateEntity
import io.golos.domain.use_cases.UseCase
import io.golos.domain.repositories.AuthStateRepository
import io.golos.domain.requestmodel.*
import javax.inject.Inject

interface PushNotificationsSettingsUseCase : UseCase<QueryResult<PushNotificationsStateModel>> {
    val getReadinessLiveData: LiveData<Boolean>

    fun subscribeToPushNotifications()
    fun unsubscribeFromPushNotifications()

}

class PushNotificationsSettingsUseCaseImpl
@Inject
constructor(
    private val pushRepository: Repository<PushNotificationsStateEntity, PushNotificationsStateUpdateRequest>,
    private val authRepository: AuthStateRepository,
    private val keyValueStorage: KeyValueStorageFacade
) : PushNotificationsSettingsUseCase {

    private val readinessLiveData = MutableLiveData<Boolean>()
    private val observer = Observer<Any> {}
    private val mediator = MediatorLiveData<Any>()
    private val updateState = MutableLiveData<QueryResult<PushNotificationsStateModel>>()
    private var lastRequest: PushNotificationsStateUpdateRequest? = null

    override val getReadinessLiveData: LiveData<Boolean> = readinessLiveData

    override val getAsLiveData = updateState

    override fun subscribe() {
        super.subscribe()
        mediator.observeForever(observer)
        mediator.addSource(authRepository.getAsLiveData(authRepository.allDataRequest)) {
            readinessLiveData.value = it?.isUserLoggedIn == true
            if (it.isUserLoggedIn) {
                updateState.value = QueryResult.Success(keyValueStorage.getPushNotificationsSettings(it.user))
            }
        }

        mediator.addSource(pushRepository.updateStates) { map ->
            val lastRequest = this.lastRequest ?: return@addSource
            val lastRequestResult = map[lastRequest.id]
            updateState.value = when (lastRequestResult) {
                is QueryResult.Success -> {
                    val newSettings = PushNotificationsStateModel(lastRequest.toEnable)
                    authRepository.getAsLiveData(authRepository.allDataRequest).value?.let {
                        keyValueStorage.savePushNotificationsSettings(it.user, newSettings)
                    }
                    QueryResult.Success(newSettings)
                }
                is QueryResult.Loading -> QueryResult.Loading(PushNotificationsStateModel(lastRequest.toEnable))
                is QueryResult.Error -> QueryResult.Error(
                    lastRequestResult.error,
                    PushNotificationsStateModel(lastRequest.toEnable)
                )
                else -> null
            }
        }

    }

    override fun subscribeToPushNotifications() {
        if (readinessLiveData.value == true) {
            val params = PushNotificationsSubscribeRequest()
            pushRepository.makeAction(params)
            lastRequest = params
        }
    }

    override fun unsubscribeFromPushNotifications() {
        if (readinessLiveData.value == true) {
            authRepository.getAsLiveData(authRepository.allDataRequest).value?.let {
                val params = PushNotificationsUnsubscribeRequest(it.user)
                pushRepository.makeAction(params)
                lastRequest = params
            }
        }
    }

    override fun unsubscribe() {
        mediator.removeObserver(observer)
        mediator.removeSource(authRepository.getAsLiveData(authRepository.allDataRequest))
        mediator.removeSource(pushRepository.updateStates)
    }

}