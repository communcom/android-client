package io.golos.data.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.golos.cyber4j.model.CreateDiscussionResult
import io.golos.data.api.DiscussionsCreationApi
import io.golos.domain.DispatchersProvider
import io.golos.domain.Logger
import io.golos.domain.Repository
import io.golos.domain.entities.DiscussionCreationResultEntity
import io.golos.domain.interactors.model.DiscussionIdModel
import io.golos.domain.model.*
import io.golos.domain.rules.CyberToEntityMapper
import io.golos.domain.rules.EntityToCyberMapper
import kotlinx.coroutines.*
import java.util.*
import kotlin.collections.HashMap

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-04-01.
 */
class DiscussionCreationRepository(
    private val discussionsCreationApi: DiscussionsCreationApi,
    private val dispatchersProvider: DispatchersProvider,
    private val logger: Logger,
    private val toCyberRequestMapper: EntityToCyberMapper<DiscussionCreationRequestEntity, DiscussionCreateRequest>,
    private val toEntityResultMapper: CyberToEntityMapper<CreateDiscussionResult, DiscussionCreationResultEntity>
) : Repository<DiscussionCreationResultEntity, DiscussionCreationRequestEntity> {

    private val repositoryScope = CoroutineScope(dispatchersProvider.uiDispatcher + SupervisorJob())

    private val discussionCreationResultMap = HashMap<Identifiable.Id, LiveData<DiscussionCreationResultEntity>>()
    private val updateStateLiveData =
        MutableLiveData<Map<Identifiable.Id, QueryResult<DiscussionCreationRequestEntity>>>()
    private val jobsMap = Collections.synchronizedMap(HashMap<DiscussionCreationRequestEntity, Job>())

    private val lastCreatedDiscussion = MutableLiveData<DiscussionCreationResultEntity>()

    private val lastCreateResultRequest = CommentCreationRequestEntity(
        "#stub#",
        DiscussionIdModel("#stub#", "#stub#", Long.MIN_VALUE), emptyList()
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

                val discussionCreationResult = withContext(dispatchersProvider.workDispatcher) {
                    val request = toCyberRequestMapper(params)
                    val apiAnswer = when (request) {
                        is CreateCommentRequest -> discussionsCreationApi.createComment(
                            request.body,
                            request.parentAccount,
                            request.parentPermlink,
                            request.parentDiscussionRefBlockNum,
                            request.category,
                            request.metadata,
                            request.beneficiaries,
                            request.vestPayment,
                            request.tokenProp
                        )
                        is CreatePostRequest -> discussionsCreationApi.createPost(
                            request.title, request.body, request.tags,
                            request.metadata, request.beneficiaries, request.vestPayment, request.tokenProp
                        )
                    }
                    toEntityResultMapper(apiAnswer)
                }

                (getAsLiveData(params) as MutableLiveData).value = discussionCreationResult
                updateStateLiveData.value =
                    updateStateLiveData.value.orEmpty() + (params.id to QueryResult.Success(params))
                lastCreatedDiscussion.value = discussionCreationResult

            } catch (e: Exception) {
                logger(e)
                updateStateLiveData.value =
                    updateStateLiveData.value.orEmpty() + (params.id to QueryResult.Error(e, params))
            }
        }.let { job ->
            jobsMap[params]?.cancel()
            jobsMap[params] = job
        }
    }

    override val updateStates: LiveData<Map<Identifiable.Id, QueryResult<DiscussionCreationRequestEntity>>>
        get() = updateStateLiveData
}