package io.golos.data.repositories.discussion_creation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.golos.commun4j.abi.implementation.comn.gallery.CreatemssgComnGalleryStruct
import io.golos.commun4j.abi.implementation.comn.gallery.DeletemssgComnGalleryStruct
import io.golos.commun4j.abi.implementation.comn.gallery.UpdatemssgComnGalleryStruct
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
import io.golos.domain.mappers.CommunToEntityMapper
import io.golos.domain.mappers.EntityToCommunMapper
import kotlinx.coroutines.*
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
    discussionsCreationApi: DiscussionsCreationApi,
    transactionsApi: TransactionsApi,
    dispatchersProvider: DispatchersProvider,
    private val logger: Logger,
    private val toAppErrorMapper: CyberToAppErrorMapper
) : DiscussionCreationRepositoryBase(
    dispatchersProvider,
    discussionsCreationApi,
    transactionsApi
),  Repository<DiscussionCreationResultEntity, DiscussionCreationRequestEntity> {

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

                val discussionCreationResult = createOrUpdateDiscussion(params)

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