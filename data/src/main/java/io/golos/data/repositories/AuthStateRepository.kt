package io.golos.data.repositories

import android.app.backup.BackupManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.golos.cyber4j.services.model.AuthResult
import io.golos.cyber4j.utils.AuthUtils
import io.golos.cyber4j.utils.StringSigner
import io.golos.data.api.AuthApi
import io.golos.data.toCyberName
import io.golos.data.toCyberUser
import io.golos.domain.*
import io.golos.domain.dependency_injection.scopes.ApplicationScope
import io.golos.domain.entities.AuthState
import io.golos.domain.entities.CyberUser
import io.golos.domain.entities.UserKeyType
import io.golos.domain.requestmodel.AuthRequest
import io.golos.domain.requestmodel.Identifiable
import io.golos.domain.requestmodel.LogOutRequest
import io.golos.domain.requestmodel.QueryResult
import io.golos.sharedmodel.CyberName
import kotlinx.coroutines.*
import java.util.*
import javax.inject.Inject
import kotlin.collections.HashMap

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-03-20.
 */
@ApplicationScope
class AuthStateRepository
@Inject
constructor(
    private val authApi: AuthApi,
    private val dispatchersProvider: DispatchersProvider,
    private val logger: Logger,
    private val keyValueStorage: KeyValueStorageFacade,
    private val userKeyStore: UserKeyStore
) : Repository<AuthState, AuthRequest> {

    private val repositoryScope = CoroutineScope(dispatchersProvider.uiDispatcher + SupervisorJob())

    private val authRequestsLiveData = MutableLiveData<Map<Identifiable.Id, QueryResult<AuthRequest>>>()
    private val authState = MutableLiveData<AuthState>()
    private val authJobsMap = Collections.synchronizedMap(HashMap<Identifiable.Id, Job>())

    init {
        makeAction(getEmptyRequest())
    }

    override fun getAsLiveData(params: AuthRequest): LiveData<AuthState> = authState

    override fun makeAction(params: AuthRequest) {
        lateinit var newParams: AuthRequest

        repositoryScope.launch {
            if (params is LogOutRequest) {
                val logOutState = AuthState("".toCyberName(), false, false, false, false)
                withContext(dispatchersProvider.ioDispatcher) {
                    keyValueStorage.saveAuthState(logOutState)
                }
                authState.value = logOutState
                return@launch
            }

            newParams = if(params.isEmpty()) {
                loadAuthRequest()
            } else {
                params
            }

            if(newParams.isEmpty()) {
                authState.value = AuthState("".toCyberName(), false, false, false, false)   // User is not logged in
                return@launch
            }

            if (authState.value == null) {
                authState.value = AuthState("".toCyberName(), false, false, false, false)
            }
            else if (authState.value?.isUserLoggedIn == true) {
                authRequestsLiveData.value =
                    authRequestsLiveData.value.orEmpty() + (newParams.id to QueryResult.Error(
                        java.lang.IllegalStateException(
                            "user $newParams is logged in, reauth is not supported"
                        ), newParams
                    ))
                return@launch
            }

            authRequestsLiveData.value =
                authRequestsLiveData.value.orEmpty() + (newParams.id to QueryResult.Loading(newParams))

            try {
                withContext(dispatchersProvider.workDispatcher) {
                    AuthUtils.checkPrivateWiF(newParams.activeKey)
                }
            } catch (e: IllegalArgumentException) {
                logger(e)
                authRequestsLiveData.value =
                    authRequestsLiveData.value.orEmpty() + (newParams.id to QueryResult.Error(
                        java.lang.IllegalArgumentException(
                            "wrong or malformed key"
                        ), newParams
                    ))
                return@launch
            }

            try {
                val account =
                    withContext(dispatchersProvider.ioDispatcher) {
                        try {
                            authApi.getUserAccount(newParams.user.userId.toCyberName())
                        } catch (e: Throwable) {
                            val userName = authApi.resolveCanonicalCyberName(newParams.user.userId)
                            authApi.getUserAccount(userName.userId)
                        }

                    }

                if (account.account_name.isEmpty()) {
                    authRequestsLiveData.value =
                        authRequestsLiveData.value.orEmpty() + (newParams.id to QueryResult.Error(
                            java.lang.IllegalStateException(
                                "account ${newParams.user} not found"
                            ), newParams
                        ))
                    return@launch
                }

                val activeKey = withContext(dispatchersProvider.workDispatcher) {
                    account.permissions.find { it.perm_name.compareTo("active") == 0 }
                }

                if (activeKey == null) {
                    authRequestsLiveData.value =
                        authRequestsLiveData.value.orEmpty() + (newParams.id to QueryResult.Error(
                            java.lang.IllegalStateException(
                                "account  ${newParams.user} has no active permissions"
                            ), newParams
                        ))
                    return@launch
                }

                val publicActiveKeyFromServer = activeKey.required_auth.keys.firstOrNull()?.key

                if (publicActiveKeyFromServer == null) {
                    authRequestsLiveData.value =
                        authRequestsLiveData.value.orEmpty() + (newParams.id to QueryResult.Error(
                            java.lang.IllegalStateException(
                                "account  ${newParams.user} has no active permission key"
                            ), newParams
                        ))
                    return@launch
                }

                val isWiFsValid = withContext(dispatchersProvider.workDispatcher) {
                    AuthUtils.isWiFsValid(newParams.activeKey, publicActiveKeyFromServer)
                }

                if (!isWiFsValid) {
                    authRequestsLiveData.value =
                        authRequestsLiveData.value.orEmpty() + (newParams.id to QueryResult.Error(
                            IllegalArgumentException("account keys not matches"), newParams
                        ))
                    return@launch
                }


            } catch (e: Exception) {
                logger(e)
                authRequestsLiveData.value =
                    authRequestsLiveData.value.orEmpty() + (newParams.id to QueryResult.Error(
                        e, newParams
                    ))
                return@launch
            }


            try {
                val authResult = auth(newParams.user.userId, newParams.activeKey)!!
                withContext(dispatchersProvider.ioDispatcher) {
                    authApi.setActiveUserCreds(authResult.user, newParams.activeKey)
                }

                onAuthSuccess(authResult.user, newParams.user)
            } catch (e: Exception) {
                logger(e)
                authRequestsLiveData.value =
                    authRequestsLiveData.value.orEmpty() + (newParams.id to QueryResult.Error(e, newParams))
            }

            val rawAuthData = authRequestsLiveData.value.orEmpty()

            val copy = withContext(dispatchersProvider.workDispatcher) {
                rawAuthData.toMutableMap()
                    .also {
                        it.values.map { queryResult ->
                            if (queryResult is QueryResult.Loading) QueryResult.Error(
                                IllegalStateException("only one auth request at a time may proceed"),
                                queryResult.originalQuery
                            )
                            else queryResult
                        }
                    }
            }

            authRequestsLiveData.value = copy

        }.let { job ->
            authJobsMap.entries.forEach {
                it.value?.cancel()
                authJobsMap[newParams.id] = job
            }
        }
    }

    override val updateStates: LiveData<Map<Identifiable.Id, QueryResult<AuthRequest>>> =
        authRequestsLiveData.distinctUntilChanged()

    override val allDataRequest: AuthRequest
            by lazy {
                AuthRequest("destroyer2k@golos".toCyberUser(), "5JagnCwCrB2sWZw6zCvaBw51ifoQuNaKNsDovuGz96wU3tUw7hJ")
            }

    private suspend fun auth(name: String, key: String): AuthResult? =
        withContext(dispatchersProvider.ioDispatcher) {
            try {
                val secret = authApi.getAuthSecret()
                authApi.authWithSecret(
                    name,
                    secret.secret,
                    StringSigner.signString(secret.secret, key)
                )
            } catch (e: Exception) {
                onAuthFail(e)
                null
            }
        }

    private suspend fun onAuthSuccess(resolvedName: CyberName, originalName: CyberUser) {
        val loadingQuery = withContext(dispatchersProvider.workDispatcher) {
            authRequestsLiveData.value?.entries?.find {
                val loadingUser = (it.value as? QueryResult.Loading)?.originalQuery?.user?.userId
                loadingUser != null && (loadingUser == originalName.userId)
            }
        }

        val oldAuthState = withContext(dispatchersProvider.ioDispatcher) {
            keyValueStorage.getAuthState()
        }

        if (loadingQuery != null) {
            val finalAuthState = AuthState(
                resolvedName,
                true,
                oldAuthState?.isPinCodeSettingsPassed ?: false,
                oldAuthState?.isFingerprintSettingsPassed ?: false,
                oldAuthState?.isKeysExported ?: false)

            authState.value = finalAuthState

            val originalLoadingQuery = loadingQuery.value as QueryResult.Loading
            authRequestsLiveData.value =
                authRequestsLiveData.value.orEmpty() + (loadingQuery.key to QueryResult.Success(
                    originalLoadingQuery.originalQuery
                ))

            withContext(dispatchersProvider.ioDispatcher) {
                keyValueStorage.saveAuthState(finalAuthState)
            }
        }
    }

    private fun onAuthFail(e: Exception) {
        logger(e)

        repositoryScope.launch {
            authState.value = AuthState("".toCyberName(), false, false, false, false)
            val loadingQuery =
                authRequestsLiveData.value?.entries?.findLast { it.value is QueryResult.Loading }

            if (loadingQuery != null) {
                authRequestsLiveData.value =
                    authRequestsLiveData.value.orEmpty() +
                            (loadingQuery.key to QueryResult.Error(
                                e, (loadingQuery.value as QueryResult.Loading).originalQuery
                            ))
            }
        }
    }

    private suspend fun loadAuthRequest(): AuthRequest {
        val authSavedAuthState = withContext(dispatchersProvider.ioDispatcher) {
            keyValueStorage.getAuthState()
        }

        return if (authSavedAuthState?.isUserLoggedIn != true) {
            getEmptyRequest()
        } else {
            val key = withContext(dispatchersProvider.ioDispatcher) {
                userKeyStore.getKey(UserKeyType.ACTIVE)
            }

            AuthRequest(authSavedAuthState.user.toCyberUser(), key)
        }
    }

    private fun getEmptyRequest() = AuthRequest(CyberUser(""), "")

    private fun AuthRequest.isEmpty() = this.user.userId == "" && this.activeKey == ""
}