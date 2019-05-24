package io.golos.data.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.golos.data.api.VoteApi
import io.golos.data.toCyberName
import io.golos.domain.DispatchersProvider
import io.golos.domain.Logger
import io.golos.domain.Repository
import io.golos.domain.entities.DiscussionIdEntity
import io.golos.domain.entities.VoteRequestEntity
import io.golos.domain.requestmodel.Identifiable
import io.golos.domain.requestmodel.QueryResult
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
) : Repository<VoteRequestEntity, VoteRequestEntity> {

    private val repositoryScope = CoroutineScope(dispatchersProvider.uiDispatcher + SupervisorJob())
    private val votingStates = MutableLiveData<Map<Identifiable.Id, QueryResult<VoteRequestEntity>>>()
    private val jobsMap = Collections.synchronizedMap(HashMap<Identifiable.Id, Job>())
    private val lastSuccessFullyVotedItem = MutableLiveData<VoteRequestEntity>()

    override fun getAsLiveData(params: VoteRequestEntity): LiveData<VoteRequestEntity> {
        return lastSuccessFullyVotedItem
    }

    init {
        votingStates.value = Collections.synchronizedMap(HashMap())
    }

    override fun makeAction(params: VoteRequestEntity) {
        repositoryScope.launch {
            try {
                val oldVotingStates = getCurrentValue()
                val loadingPair = params.id to QueryResult.Loading(params)

                votingStates.value = oldVotingStates  + loadingPair

                withContext(dispatchersProvider.workDispatcher) {
                    voteApi.vote(
                        params.discussionIdEntity.userId.toCyberName(),
                        params.discussionIdEntity.permlink,
                        params.power
                    )
                }
                delay(3_000)
                votingStates.value = getCurrentValue() + (params.id to QueryResult.Success(params))
                lastSuccessFullyVotedItem.value = params


            } catch (e: Exception) {
                votingStates.value = getCurrentValue() + (params.id to QueryResult.Error(e, params))
                logger(e)
            }

        }.let { job ->
            jobsMap[params.id]?.cancel()
            jobsMap[params.id] = job
        }
    }

    private fun getCurrentValue() = votingStates.value.orEmpty()

    override val allDataRequest: VoteRequestEntity
            by lazy {
                VoteRequestEntity.VoteForAPostRequestEntity(0, DiscussionIdEntity("stub", "stub"))
            }
    override val updateStates: LiveData<Map<Identifiable.Id, QueryResult<VoteRequestEntity>>>
        get() = votingStates
}