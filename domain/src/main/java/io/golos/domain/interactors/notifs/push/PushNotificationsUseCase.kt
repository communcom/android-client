package io.golos.domain.interactors.notifs.push

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import io.golos.domain.Repository
import io.golos.domain.entities.AuthState
import io.golos.domain.entities.PushNotificationsStateEntity
import io.golos.domain.interactors.UseCase
import io.golos.domain.requestmodel.AuthRequest
import io.golos.domain.requestmodel.PushNotificationsRequest
import io.golos.domain.requestmodel.PushNotificationsStateModel
import io.golos.domain.requestmodel.QueryResult

interface PushNotificationsUseCase : UseCase<QueryResult<PushNotificationsStateModel>> {
    fun subscribeToPushNotifications()
    fun unsubscribeFromPushNotifications()
}

class PushNotificationsUseCaseImpl(
    private val pushRepository: Repository<PushNotificationsStateEntity, PushNotificationsRequest>,
    private val authRepository: Repository<AuthState, AuthRequest>
) : PushNotificationsUseCase {

    private val readinessLiveData = MutableLiveData<Boolean>()
    private val observer = Observer<Any> {}
    private val mediator = MediatorLiveData<Any>()
    private val updateState = MutableLiveData<QueryResult<PushNotificationsStateModel>>()
    private var lastRequest: PushNotificationsRequest? = null

    val getReadinessLiveData: LiveData<Boolean> = readinessLiveData

    override val getAsLiveData = updateState

    override fun subscribe() {
        super.subscribe()
        mediator.observeForever(observer)
        mediator.addSource(authRepository.getAsLiveData(authRepository.allDataRequest)) {
            readinessLiveData.value = it?.isUserLoggedIn == true
        }

        mediator.addSource(pushRepository.updateStates) { map ->
            val lastRequest = this.lastRequest ?: return@addSource
            val lastRequestResult = map[lastRequest.id]
            updateState.value = when (lastRequestResult) {
                is QueryResult.Success -> QueryResult.Success(PushNotificationsStateModel(lastRequest.toEnable))
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
            val params = PushNotificationsRequest(true)
            pushRepository.makeAction(params)
            lastRequest = params
        }
    }

    override fun unsubscribeFromPushNotifications() {
        if (readinessLiveData.value == true) {
            val params = PushNotificationsRequest(false)
            pushRepository.makeAction(params)
            lastRequest = params
        }
    }

    override fun unsubscribe() {
        mediator.removeObserver(observer)
    }

}