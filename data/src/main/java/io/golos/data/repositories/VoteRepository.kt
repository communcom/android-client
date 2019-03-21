package io.golos.data.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.golos.data.api.VoteApi
import io.golos.data.toCyberName
import io.golos.domain.DispatchersProvider
import io.golos.domain.Logger
import io.golos.domain.Repository
import io.golos.domain.entities.VoteRequest
import io.golos.domain.model.Identifiable
import io.golos.domain.model.QueryResult
import kotlinx.coroutines.*
import java.util.*
import kotlin.collections.HashMap

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-03-21.
 */
class VoteRepository(
    private val voteApi: VoteApi,
    private val dispatchersProvider: DispatchersProvider,
    private val logger: Logger
) : Repository<VoteRequest, VoteRequest> {

    private val repositoryScope = CoroutineScope(dispatchersProvider.uiDispatcher + SupervisorJob())
    private val votingStates = MutableLiveData<Map<Identifiable.Id, QueryResult<VoteRequest>>>()
    private val jobsMap = Collections.synchronizedMap(HashMap<Identifiable.Id, Job>())

    override fun getAsLiveData(params: VoteRequest): LiveData<VoteRequest> {
        return MutableLiveData()
    }

    override fun makeAction(params: VoteRequest) {
        repositoryScope.launch {
            try {
                votingStates.value = votingStates.value.orEmpty() + (params.id to QueryResult.Loading(params))
                withContext(dispatchersProvider.workDispatcher) {
                    voteApi.vote(
                        params.discussionIdEntity.userId.toCyberName(),
                        params.discussionIdEntity.permlink,
                        params.discussionIdEntity.refBlockNum,
                        params.power
                    )
                }
                votingStates.value = votingStates.value.orEmpty() + (params.id to QueryResult.Success(params))

            } catch (e: Exception) {
                votingStates.value = votingStates.value.orEmpty() + (params.id to QueryResult.Error(e, params))
                logger(e)
            }

        }.let { job ->
            jobsMap[params.id]?.cancel()
            jobsMap[params.id] = job
        }
    }

    override val updateStates: LiveData<Map<Identifiable.Id, QueryResult<VoteRequest>>>
        get() = votingStates
}