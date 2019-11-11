package io.golos.data.repositories.discussion.live_data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.golos.data.api.discussions.DiscussionsApi
import io.golos.data.api.transactions.TransactionsApi
import io.golos.data.errors.CyberToAppErrorMapper
import io.golos.data.repositories.discussion.DiscussionCreationRepositoryBase
import io.golos.domain.DispatchersProvider
import io.golos.domain.commun_entities.Permlink
import io.golos.domain.repositories.Repository
import io.golos.domain.dependency_injection.scopes.ApplicationScope
import io.golos.domain.dto.DiscussionCreationResultEntity
import io.golos.domain.use_cases.model.DiscussionIdModel
import io.golos.domain.requestmodel.*
import kotlinx.coroutines.*
import timber.log.Timber
import java.util.*
import javax.inject.Inject
import kotlin.collections.HashMap

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-04-01.
 */
@ApplicationScope
class DiscussionCreationRepositoryLiveData
@Inject
constructor(
    discussionsCreationApi: DiscussionsApi,
    transactionsApi: TransactionsApi,
    private val dispatchersProvider: DispatchersProvider,
    private val toAppErrorMapper: CyberToAppErrorMapper
) : DiscussionCreationRepositoryBase(
    discussionsCreationApi,
    transactionsApi
), Repository<DiscussionCreationResultEntity, DiscussionCreationRequestEntity> {

    private val repositoryScope = CoroutineScope(dispatchersProvider.uiDispatcher + SupervisorJob())

    private val discussionCreationResultMap = HashMap<Identifiable.Id, LiveData<DiscussionCreationResultEntity>>()
    private val updateStateLiveData =
        MutableLiveData<Map<Identifiable.Id, QueryResult<DiscussionCreationRequestEntity>>>()
    private val jobsMap = Collections.synchronizedMap(HashMap<DiscussionCreationRequestEntity, Job>())

    private val lastCreatedDiscussion = MutableLiveData<DiscussionCreationResultEntity>()

    private val lastCreateResultRequest = CommentCreationRequestEntity(
        "#stub#",
        DiscussionIdModel("#stub#", Permlink("#stub#")), emptyList()
    )

    override val allDataRequest: DiscussionCreationRequestEntity
        get() = lastCreateResultRequest

    override fun getAsLiveData(params: DiscussionCreationRequestEntity): LiveData<DiscussionCreationResultEntity> {
        return if (params == lastCreateResultRequest) lastCreatedDiscussion
        else discussionCreationResultMap.getOrPut(params.id) { MutableLiveData() }
    }

    override fun makeAction(params: DiscussionCreationRequestEntity) {
        repositoryScope.launch {
            try {
                updateStateLiveData.value =
                    updateStateLiveData.value.orEmpty() + (params.id to QueryResult.Loading(params))

                val discussionCreationResult = withContext(dispatchersProvider.ioDispatcher) {
                    createOrUpdateDiscussion(params)
                }

                (getAsLiveData(params) as MutableLiveData).value = discussionCreationResult
                updateStateLiveData.value =
                    updateStateLiveData.value.orEmpty() + (params.id to QueryResult.Success(params))
                lastCreatedDiscussion.value = discussionCreationResult

            } catch (e: Exception) {
                Timber.e(e)
                updateStateLiveData.value =
                    updateStateLiveData.value.orEmpty() +
                            (params.id to QueryResult.Error(
                                toAppErrorMapper.mapIfNeeded(e),
                                params
                            ))
            }
        }.let { job ->
            jobsMap[params]?.cancel()
            jobsMap[params] = job
        }
    }

    override val updateStates: LiveData<Map<Identifiable.Id, QueryResult<DiscussionCreationRequestEntity>>>
        get() = updateStateLiveData
}