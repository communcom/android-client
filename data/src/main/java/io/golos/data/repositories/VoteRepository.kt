package io.golos.data.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.golos.data.api.transactions.TransactionsApi
import io.golos.data.api.vote.VoteApi
import io.golos.data.errors.CyberToAppErrorMapper
import io.golos.data.toCyberName
import io.golos.domain.DispatchersProvider
import io.golos.domain.commun_entities.Permlink
import io.golos.domain.repositories.Repository
import io.golos.domain.dependency_injection.scopes.ApplicationScope
import io.golos.domain.entities.DiscussionIdEntity
import io.golos.domain.entities.VoteRequestEntity
import io.golos.domain.requestmodel.Identifiable
import io.golos.domain.requestmodel.QueryResult
import kotlinx.coroutines.*
import timber.log.Timber
import java.util.*
import javax.inject.Inject
import kotlin.collections.HashMap

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-03-21.
 */
@ApplicationScope
class VoteRepository
@Inject
constructor(
    private val voteApi: VoteApi,
    private val transactionsApi: TransactionsApi,
    private val dispatchersProvider: DispatchersProvider,
    private val toAppErrorMapper: CyberToAppErrorMapper
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

                votingStates.value = oldVotingStates + loadingPair

                withContext(dispatchersProvider.calculationsDispatcher) {
                    val transactionResult = voteApi.vote(
                        params.discussionIdEntity.userId.toCyberName(),
                        params.discussionIdEntity.permlink,
                        params.power
                    )
                    transactionsApi.waitForTransaction(transactionResult.transaction_id)
                }
                votingStates.value = getCurrentValue() + (params.id to QueryResult.Success(params))
                lastSuccessFullyVotedItem.value = params


            } catch (e: Exception) {
                votingStates.value = getCurrentValue() + (params.id to QueryResult.Error(toAppErrorMapper.mapIfNeeded(e), params))
                Timber.e(e)
            }

        }.let { job ->
            jobsMap[params.id]?.cancel()
            jobsMap[params.id] = job
        }
    }

    private fun getCurrentValue() = votingStates.value.orEmpty()

    override val allDataRequest: VoteRequestEntity
            by lazy {
                VoteRequestEntity.VoteForAPostRequestEntity(0, DiscussionIdEntity("stub", Permlink("stub")))
            }
    override val updateStates: LiveData<Map<Identifiable.Id, QueryResult<VoteRequestEntity>>>
        get() = votingStates
}