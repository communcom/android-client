package io.golos.data.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.golos.cyber4j.abi.implementation.gls.publish.CreatemssgGlsPublishStruct
import io.golos.cyber4j.abi.implementation.gls.publish.DeletemssgGlsPublishStruct
import io.golos.cyber4j.abi.implementation.gls.publish.UpdatemssgGlsPublishStruct
import io.golos.data.api.DiscussionsCreationApi
import io.golos.data.api.TransactionsApi
import io.golos.data.errors.CyberToAppErrorMapper
import io.golos.domain.DispatchersProvider
import io.golos.domain.Logger
import io.golos.domain.Repository
import io.golos.domain.dependency_injection.scopes.ApplicationScope
import io.golos.domain.entities.DeleteDiscussionResultEntity
import io.golos.domain.entities.DiscussionCreationResultEntity
import io.golos.domain.entities.UpdatePostResultEntity
import io.golos.domain.interactors.model.DiscussionIdModel
import io.golos.domain.requestmodel.*
import io.golos.domain.rules.CyberToEntityMapper
import io.golos.domain.rules.EntityToCyberMapper
import kotlinx.coroutines.*
import java.net.SocketTimeoutException
import java.util.*
import javax.inject.Inject
import kotlin.collections.HashMap

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-04-01.
 */
@ApplicationScope
class DiscussionCreationRepository
@Inject
constructor(
    private val discussionsCreationApi: DiscussionsCreationApi,
    private val transactionsApi: TransactionsApi,
    private val dispatchersProvider: DispatchersProvider,
    private val logger: Logger,
    private val toCyberRequestMapper: EntityToCyberMapper<DiscussionCreationRequestEntity, DiscussionCreateRequest>,
    private val toEntityResultMapper: CyberToEntityMapper<CreatemssgGlsPublishStruct, DiscussionCreationResultEntity>,
    private val toEntityUpdateResultMapper: CyberToEntityMapper<UpdatemssgGlsPublishStruct, UpdatePostResultEntity>,
    private val toEntityDeleteResultMapper: CyberToEntityMapper<DeletemssgGlsPublishStruct, DeleteDiscussionResultEntity>,
    private val toAppErrorMapper: CyberToAppErrorMapper

) : Repository<DiscussionCreationResultEntity, DiscussionCreationRequestEntity> {

    private val repositoryScope = CoroutineScope(dispatchersProvider.uiDispatcher + SupervisorJob())

    private val discussionCreationResultMap = HashMap<Identifiable.Id, LiveData<DiscussionCreationResultEntity>>()
    private val updateStateLiveData =
        MutableLiveData<Map<Identifiable.Id, QueryResult<DiscussionCreationRequestEntity>>>()
    private val jobsMap = Collections.synchronizedMap(HashMap<DiscussionCreationRequestEntity, Job>())

    private val lastCreatedDiscussion = MutableLiveData<DiscussionCreationResultEntity>()

    private val lastCreateResultRequest = CommentCreationRequestEntity(
        "#stub#",
        DiscussionIdModel("#stub#", "#stub#"), emptyList()
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

                val discussionCreationResult = withContext(dispatchersProvider.calculationsDispatcher) {
                    val request = toCyberRequestMapper(params)
                    val apiAnswer = when (request) {
                        is CreateCommentRequest -> discussionsCreationApi.createComment(
                            request.body,
                            request.parentAccount,
                            request.parentPermlink,
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
                        is UpdatePostRequest -> discussionsCreationApi.updatePost(
                            request.postPermlink, request.title, request.body,
                            request.tags, request.metadata
                        )
                        is DeleteDiscussionRequest -> discussionsCreationApi.deletePostOrComment(request.permlink)
                    }
                    try {
                        transactionsApi.waitForTransaction(apiAnswer.first.transaction_id)
                    } catch (e: SocketTimeoutException) {
                        //for now SocketTimeoutException during waitForTransaction phase counts as a
                        //success, so we just log it and ignore
                        logger.log(e)
                    }

                    when (request) {
                        is UpdatePostRequest -> toEntityUpdateResultMapper(apiAnswer.second as UpdatemssgGlsPublishStruct)
                        is DeleteDiscussionRequest -> toEntityDeleteResultMapper(apiAnswer.second as DeletemssgGlsPublishStruct)
                        else -> toEntityResultMapper(apiAnswer.second as CreatemssgGlsPublishStruct)
                    }
                }

                (getAsLiveData(params) as MutableLiveData).value = discussionCreationResult
                updateStateLiveData.value =
                    updateStateLiveData.value.orEmpty() + (params.id to QueryResult.Success(params))
                lastCreatedDiscussion.value = discussionCreationResult

            } catch (e: Exception) {
                logger.log(e)
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