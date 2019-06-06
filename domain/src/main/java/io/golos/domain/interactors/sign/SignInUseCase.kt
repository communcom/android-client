package io.golos.domain.interactors.sign

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import io.golos.cyber4j.model.CyberName
import io.golos.domain.DispatchersProvider
import io.golos.domain.Repository
import io.golos.domain.distinctUntilChanged
import io.golos.domain.entities.AuthState
import io.golos.domain.entities.CyberUser
import io.golos.domain.interactors.UseCase
import io.golos.domain.interactors.model.UserAuthState
import io.golos.domain.map
import io.golos.domain.requestmodel.*
import kotlinx.coroutines.*

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-03-29.
 */
class SignInUseCase(
    private val authRepo: Repository<AuthState, AuthRequest>,
    private val dispatcher: DispatchersProvider
) : UseCase<UserAuthState> {
    private val authState = MutableLiveData<UserAuthState>()
    private val authLoadingState = MutableLiveData<Map<CyberUser, QueryResult<AuthRequestModel>>>()
    private val observer = Observer<Any> {}
    private val mediator = MediatorLiveData<Any>()

    override val getAsLiveData: LiveData<UserAuthState> = authState.distinctUntilChanged()
    private val signInStateLiveData = MutableLiveData<SignInState>()

    private val coroutineScope = CoroutineScope(dispatcher.uiDispatcher + SupervisorJob())
    private var authJob: Job? = null

    val getLogInStates: LiveData<Map<CyberUser, QueryResult<AuthRequestModel>>> =
        authLoadingState.distinctUntilChanged()

    val getSignInState: LiveData<SignInState> = signInStateLiveData.distinctUntilChanged()

    override fun subscribe() {
        super.subscribe()
        mediator.observeForever(observer)
        mediator.addSource(authRepo.getAsLiveData(authRepo.allDataRequest)) {

            authState.value =
                UserAuthState(it.isUserLoggedIn, it.user)

            updateLogInState()

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


            updateLogInState()
        }

    }

    private fun updateLogInState() {
        authJob?.cancel()
        authJob = coroutineScope.launch {

            delay(100)
            val authStateInRepository =
                authRepo.getAsLiveData(authRepo.allDataRequest).value ?: AuthState(CyberName(""), false)
            val updateStateInRepository = authRepo.updateStates.value.orEmpty().values

            signInStateLiveData.value = when {
                updateStateInRepository.any { it is QueryResult.Success } && authStateInRepository.isUserLoggedIn -> SignInState.USER_LOGGED_IN
                updateStateInRepository.any { it is QueryResult.Loading } -> SignInState.LOADING
                else -> SignInState.LOG_IN_NEEDED
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
        authRepo.makeAction(AuthRequest(CyberUser(request.user.userId.trim()), request.activeKey.trim()))
    }

    fun logOut() {
        authRepo.makeAction(LogOutRequest())
    }
}