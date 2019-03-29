package io.golos.domain.interactors.sign

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import io.golos.cyber4j.model.CyberName
import io.golos.domain.Repository
import io.golos.domain.distinctUntilChanged
import io.golos.domain.entities.AuthState
import io.golos.domain.entities.CyberUser
import io.golos.domain.interactors.UseCase
import io.golos.domain.interactors.model.UserAuthState
import io.golos.domain.map
import io.golos.domain.model.AuthRequest
import io.golos.domain.model.AuthRequestModel
import io.golos.domain.model.QueryResult

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-03-29.
 */
class SignInUseCase(private val authRepo: Repository<AuthState, AuthRequest>) : UseCase<UserAuthState> {
    private val authState = MutableLiveData<UserAuthState>()
    private val authLoadingState = MutableLiveData<Map<CyberUser, QueryResult<AuthRequestModel>>>()
    private val observer = Observer<Any> {}
    private val mediator = MediatorLiveData<Any>()

    override val getAsLiveData: LiveData<UserAuthState> = authState.distinctUntilChanged()

    val getLogInStates: LiveData<Map<CyberUser, QueryResult<AuthRequestModel>>> =
        authLoadingState.distinctUntilChanged()

    override fun subscribe() {
        super.subscribe()
        mediator.observeForever(observer)
        mediator.addSource(authRepo.getAsLiveData(AuthRequest(CyberUser(""), ""))) {
            authState.value = UserAuthState(it.isUserLoggedIn, CyberName(it.user.userId))
        }
        mediator.addSource(authRepo.updateStates) {
            authLoadingState.value = it.orEmpty().map { mapEntry ->
                mapEntry.value.map(
                    AuthRequestModel(
                        mapEntry.value.originalQuery.user,
                        mapEntry.value.originalQuery.activeKey
                    )
                )
            }
                .associateBy { queryResult ->
                    queryResult.originalQuery.user
                }
        }
    }

    override fun unsubscribe() {
        super.unsubscribe()
        mediator.removeObserver(observer)
        mediator.removeSource(authRepo.getAsLiveData(AuthRequest(CyberUser(""), "")))
        mediator.removeSource(authRepo.updateStates)
    }

    fun authWithCredentials(request: AuthRequestModel) {
        authRepo.makeAction(AuthRequest(request.user, request.activeKey))
    }
}