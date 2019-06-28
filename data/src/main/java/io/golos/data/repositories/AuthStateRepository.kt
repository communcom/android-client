package io.golos.data.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.golos.cyber4j.services.model.AuthResult
import io.golos.cyber4j.utils.AuthUtils
import io.golos.cyber4j.utils.StringSigner
import io.golos.data.api.AuthApi
import io.golos.data.toCyberName
import io.golos.data.toCyberUser
import io.golos.domain.*
import io.golos.domain.entities.AuthState
import io.golos.domain.entities.CyberUser
import io.golos.domain.requestmodel.AuthRequest
import io.golos.domain.requestmodel.Identifiable
import io.golos.domain.requestmodel.LogOutRequest
import io.golos.domain.requestmodel.QueryResult
import io.golos.sharedmodel.CyberName
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
        authState.value = AuthState("".toCyberName(), false)

        val authSavedAuthState = persister.getAuthState()
        val key = persister.getActiveKey()

        if (authSavedAuthState?.isUserLoggedIn == true && key != null) {
            makeAction(AuthRequest(authSavedAuthState.user.toCyberUser(), key))
        }
    }

    private fun auth(name: String, key: String): AuthResult? {
        try {
            val secret = authApi.getAuthSecret()
            return authApi.authWithSecret(
                name,
                secret.secret,
                StringSigner.signString(secret.secret, key)
            )
        } catch (e: Exception) {
            onAuthFail(e)
        }
        return null
    }

    private fun onAuthSuccess(resolvedName: CyberName, originalName: CyberUser) {
        val loadingQuery =
            authRequestsLiveData.value?.entries?.find {
                val loadingUser = (it.value as? QueryResult.Loading)?.originalQuery?.user?.userId
                loadingUser != null && (loadingUser == originalName.userId)
            }

        if (loadingQuery != null) {
            val finalAuthState = AuthState(resolvedName, true)
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


    private fun onAuthFail(e: Exception) {
        logger(e)

        repositoryScope.launch {
            authState.value = AuthState("".toCyberName(), false)
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

    override fun getAsLiveData(params: AuthRequest): LiveData<AuthState> {
        return authState
    }

    override fun makeAction(params: AuthRequest) {
        repositoryScope.launch {

            if (params is LogOutRequest) {
                val logOutState = AuthState("".toCyberName(), false)
                persister.saveAuthState(logOutState)
                authState.value = logOutState
                return@launch
            }

            if (authState.value == null) authState.value = AuthState("".toCyberName(), false)
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
                    withContext(dispatchersProvider.workDispatcher) {
                        try {
                            authApi.getUserAccount(params.user.userId.toCyberName())
                        } catch (e: Throwable) {
                            val userName = authApi.resolveCanonicalCyberName(params.user.userId)
                            authApi.getUserAccount(userName.userId)
                        }

                    }

                if (account.account_name.isEmpty()) {
                    authRequestsLiveData.value =
                        authRequestsLiveData.value.orEmpty() + (params.id to QueryResult.Error(
                            java.lang.IllegalStateException(
                                "account ${params.user} not found"
                            ), params
                        ))
                    return@launch
                }

                val activeKey = account.permissions.find { it.perm_name.compareTo("active") == 0 }

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
                    return@launch
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
                val authResult = auth(params.user.userId, params.activeKey)!!
                authApi.setActiveUserCreds(authResult.user, params.activeKey)
                onAuthSuccess(authResult.user, params.user)
            } catch (e: Exception) {
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
                AuthRequest("destroyer2k@golos".toCyberUser(), "5JagnCwCrB2sWZw6zCvaBw51ifoQuNaKNsDovuGz96wU3tUw7hJ")
            }
}