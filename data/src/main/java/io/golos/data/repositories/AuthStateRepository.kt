package io.golos.data.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.golos.cyber4j.model.AuthListener
import io.golos.cyber4j.model.CyberName
import io.golos.data.api.AuthApi
import io.golos.data.toCyberUser
import io.golos.domain.DispatchersProvider
import io.golos.domain.Logger
import io.golos.domain.Repository
import io.golos.domain.distinctUntilChanged
import io.golos.domain.entities.AuthState
import io.golos.domain.model.AuthRequest
import io.golos.domain.model.Identifiable
import io.golos.domain.model.QueryResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.HashMap

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-03-20.
 */
class AuthStateRepository(
    private val authApi: AuthApi,
    private val dispatchersProvider: DispatchersProvider,
    private val logger: Logger
) : Repository<AuthState, AuthRequest> {

    private val repositoryScope = CoroutineScope(dispatchersProvider.uiDispatcher + SupervisorJob())

    val authRequest =
        AuthRequest("fkmiiibuntct".toCyberUser(), "5JyzKR94WqFxqcExLMcakd7SksEqkNDbo2GT4VvdRs4g3XyQrg8")

    private val authRequestsLiveData = MutableLiveData<Map<Identifiable.Id, QueryResult<AuthRequest>>>()
    private val authState = MutableLiveData<AuthState>()
    private val authJobsMap = Collections.synchronizedMap(HashMap<Identifiable.Id, Job>())

    init {
        authApi.addAuthListener(object : AuthListener {
            override fun onAuthSuccess(forUser: CyberName) {

                repositoryScope.launch {
                    authState.value = AuthState(forUser.toCyberUser(), true)

                    val loadingQuery =
                        authRequestsLiveData.value?.entries?.find { (it.value as? QueryResult.Loading)?.originalQuery?.user?.userId == forUser.name }

                    if (loadingQuery != null) {
                        val originalLoadingQuery = loadingQuery.value as QueryResult.Loading
                        authRequestsLiveData.value =
                            authRequestsLiveData.value.orEmpty() + (loadingQuery.key to QueryResult.Success(
                                originalLoadingQuery.originalQuery
                            ))
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
    }

    override fun getAsLiveData(params: AuthRequest): LiveData<AuthState> {
        return authState.distinctUntilChanged()
    }

    override fun makeAction(params: AuthRequest) {
        repositoryScope.launch {
            if (authState.value == null) authState.value = AuthState("".toCyberUser(), false)

            authRequestsLiveData.value =
                authRequestsLiveData.value.orEmpty() + (params.id to QueryResult.Loading(params))

            authApi.setActiveUserCreds(CyberName(params.user.userId), params.activeKey)
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


}