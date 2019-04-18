package io.golos.data.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.golos.cyber4j.model.AuthListener
import io.golos.cyber4j.model.CyberName
import io.golos.cyber4j.utils.AuthUtils
import io.golos.data.api.AuthApi
import io.golos.data.toCyberName
import io.golos.data.toCyberUser
import io.golos.domain.*
import io.golos.domain.entities.AuthState
import io.golos.domain.model.AuthRequest
import io.golos.domain.model.Identifiable
import io.golos.domain.model.QueryResult
import kotlinx.coroutines.*
import java.util.*
import kotlin.collections.HashMap

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-03-20.
 */
class AuthStateRepository(
    private val authApi: AuthApi,
    private val dispatchersProvider: DispatchersProvider,
    private val logger: Logger,
    private val persister: Persister
) : Repository<AuthState, AuthRequest> {

    private val repositoryScope = CoroutineScope(dispatchersProvider.uiDispatcher + SupervisorJob())

    private val authRequestsLiveData = MutableLiveData<Map<Identifiable.Id, QueryResult<AuthRequest>>>()
    private val authState = MutableLiveData<AuthState>()
    private val authJobsMap = Collections.synchronizedMap(HashMap<Identifiable.Id, Job>())

    init {
        authApi.addAuthListener(object : AuthListener {
            override fun onAuthSuccess(forUser: CyberName) {

                repositoryScope.launch {

                    val loadingQuery =
                        authRequestsLiveData.value?.entries?.find { (it.value as? QueryResult.Loading)?.originalQuery?.user?.userId == forUser.name }

                    if (loadingQuery != null) {
                        val finalAuthState = AuthState(forUser.toCyberUser(), true)
                        authState.value = finalAuthState

                        val originalLoadingQuery = loadingQuery.value as QueryResult.Loading
                        authRequestsLiveData.value =
                            authRequestsLiveData.value.orEmpty() + (loadingQuery.key to QueryResult.Success(
                                originalLoadingQuery.originalQuery
                            ))

                        persister.saveAuthState(finalAuthState)
                        persister.saveActiveKey(originalLoadingQuery.originalQuery.activeKey)
                    }
                }
            }

            override fun onFail(e: Exception) {
                logger(e)

                repositoryScope.launch {
                    authState.value = AuthState("".toCyberUser(), false)
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
        })

        authState.value = AuthState("".toCyberUser(), false)

        val authSavedAuthState = persister.getAuthState()
        val key = persister.getActiveKey()

        if (authSavedAuthState?.isUserLoggedIn == true && key != null) {
            makeAction(AuthRequest(authSavedAuthState.user, key))
        }
    }

    override fun getAsLiveData(params: AuthRequest): LiveData<AuthState> {
        return authState
    }

    override fun makeAction(params: AuthRequest) {
        repositoryScope.launch {
            if (authState.value == null) authState.value = AuthState("".toCyberUser(), false)
            else if (authState.value?.isUserLoggedIn == true) {
                authRequestsLiveData.value =
                    authRequestsLiveData.value.orEmpty() + (params.id to QueryResult.Error(
                        java.lang.IllegalStateException(
                            "user $params is logged in, reauth is not supported"
                        ), params
                    ))
                return@launch
            }

            authRequestsLiveData.value =
                authRequestsLiveData.value.orEmpty() + (params.id to QueryResult.Loading(params))

            try {
                AuthUtils.checkPrivateWiF(params.activeKey)
            } catch (e: IllegalArgumentException) {
                logger(e)
                authRequestsLiveData.value =
                    authRequestsLiveData.value.orEmpty() + (params.id to QueryResult.Error(
                        java.lang.IllegalArgumentException(
                            "wrong or malformed key"
                        ), params
                    ))
                return@launch
            }

            try {
                val account =
                    withContext(dispatchersProvider.workDispatcher) { authApi.getUserAccount(params.user.userId.toCyberName()) }

                if (account.account_name.isEmpty()) {
                    authRequestsLiveData.value =
                        authRequestsLiveData.value.orEmpty() + (params.id to QueryResult.Error(
                            java.lang.IllegalStateException(
                                "account ${params.user} not found"
                            ), params
                        ))
                    return@launch
                }

                val activeKey = account.permissions.find { it.perm_name == "active" }

                if (activeKey == null) {
                    authRequestsLiveData.value =
                        authRequestsLiveData.value.orEmpty() + (params.id to QueryResult.Error(
                            java.lang.IllegalStateException(
                                "account  ${params.user} has no active permissions"
                            ), params
                        ))
                    return@launch
                }

                val publicActiveKeyFromServer = activeKey.required_auth.keys.firstOrNull()?.key

                if (publicActiveKeyFromServer == null) {
                    authRequestsLiveData.value =
                        authRequestsLiveData.value.orEmpty() + (params.id to QueryResult.Error(
                            java.lang.IllegalStateException(
                                "account  ${params.user} has no active permission key"
                            ), params
                        ))
                    return@launch
                }

                if (!AuthUtils.isWiFsValid(params.activeKey, publicActiveKeyFromServer)) {
                    authRequestsLiveData.value =
                        authRequestsLiveData.value.orEmpty() + (params.id to QueryResult.Error(
                            IllegalArgumentException("account keys not matches"), params
                        ))
                }

            } catch (e: Exception) {
                logger(e)
                authRequestsLiveData.value =
                    authRequestsLiveData.value.orEmpty() + (params.id to QueryResult.Error(
                        e, params
                    ))
                return@launch
            }

            try {
                authApi.setActiveUserCreds(CyberName(params.user.userId), params.activeKey)
            } catch (e: java.lang.Exception) {
                logger(e)
                authRequestsLiveData.value =
                    authRequestsLiveData.value.orEmpty() + (params.id to QueryResult.Error(e, params))
            }

        }.let { job ->
            authJobsMap.entries.forEach {
                it.value?.cancel()
                authJobsMap[params.id] = job

                val copy = authRequestsLiveData.value.orEmpty().toMutableMap()
                copy.values.map { queryResult ->
                    if (queryResult is QueryResult.Loading) QueryResult.Error(
                        IllegalStateException("only one auth request at a time may proceed"),
                        queryResult.originalQuery
                    )
                    else queryResult
                }
                authRequestsLiveData.value = copy
            }
        }
    }

    override val updateStates: LiveData<Map<Identifiable.Id, QueryResult<AuthRequest>>> =
        authRequestsLiveData.distinctUntilChanged()

    override val allDataRequest: AuthRequest
            by lazy {
                AuthRequest("anpacifgrlqe".toCyberUser(), "5JB6WdGo7tvArMP6u3FtwfYGzBei8wMEyaVyrACkczGrbA6BviF")
            }
}