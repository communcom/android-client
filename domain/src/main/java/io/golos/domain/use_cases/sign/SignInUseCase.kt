package io.golos.domain.use_cases.sign

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import io.golos.commun4j.sharedmodel.CyberName
import io.golos.domain.DispatchersProvider
import io.golos.domain.dto.AuthState
import io.golos.domain.dto.AuthType
import io.golos.domain.dto.CyberUser
import io.golos.domain.extensions.distinctUntilChanged
import io.golos.domain.extensions.map
import io.golos.domain.use_cases.UseCase
import io.golos.domain.use_cases.model.UserAuthState
import io.golos.domain.repositories.AuthStateRepository
import io.golos.domain.requestmodel.AuthRequest
import io.golos.domain.requestmodel.AuthRequestModel
import io.golos.domain.requestmodel.QueryResult
import io.golos.domain.requestmodel.SignInState
import kotlinx.coroutines.*
import javax.inject.Inject

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-03-29.
 */
class SignInUseCase
@Inject
constructor(
    private val authRepo: AuthStateRepository,
    dispatcher: DispatchersProvider
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

            authState.value = UserAuthState(it.isUserLoggedIn, it.user)

            updateLogInState()

        }
        mediator.addSource(authRepo.updateStates) {
            authLoadingState.value = it.orEmpty().map { mapEntry ->
                mapEntry.value.map(
                    AuthRequestModel(
                        mapEntry.value.originalQuery.userName,
                        mapEntry.value.originalQuery.user,
                        mapEntry.value.originalQuery.activeKey,
                        AuthType.SIGN_IN
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
            val authStateInRepository = authRepo.getAsLiveData(authRepo.allDataRequest).value
                ?: AuthState("", CyberName(""), false, false, false, false, AuthType.SIGN_IN)
            val updateStateInRepository = authRepo.updateStates.value.orEmpty().values

            val isSetupCompleted = with(authStateInRepository) {
                isPinCodeSettingsPassed && isFingerprintSettingsPassed && isKeysExported
            }

            signInStateLiveData.value = when {
                updateStateInRepository.any { it is QueryResult.Success } &&
                        authStateInRepository.isUserLoggedIn &&
                        isSetupCompleted -> SignInState.USER_LOGGED_IN_SETUP_COMPLETED

                updateStateInRepository.any { it is QueryResult.Success } &&
                        authStateInRepository.isUserLoggedIn &&
                        !isSetupCompleted -> SignInState.USER_LOGGED_IN_SETUP_NOT_COMPLETED

                updateStateInRepository.any { it is QueryResult.Loading } -> SignInState.LOADING

                else -> SignInState.LOG_IN_NEEDED
            }
        }
    }

    override fun unsubscribe() {
        super.unsubscribe()
        mediator.removeObserver(observer)
        mediator.removeSource(authRepo.getAsLiveData(AuthRequest("", CyberUser(""), "", AuthType.SIGN_IN)))
        mediator.removeSource(authRepo.updateStates)
    }

    fun authWithCredentials(request: AuthRequestModel) {
        authRepo.makeAction(AuthRequest(request.userName, CyberUser(request.user.userId.trim()), request.activeKey.trim(), AuthType.SIGN_IN))
    }

    fun logOut() {
        authRepo.makeAction(AuthRequest("", CyberUser(""), "", AuthType.LOG_OUT))
    }
}