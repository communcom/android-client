package io.golos.domain.use_cases.feed

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import io.golos.domain.DispatchersProvider
import io.golos.domain.dto.DiscussionEntity
import io.golos.domain.dto.DiscussionsSort
import io.golos.domain.dto.FeedEntity
import io.golos.domain.dto.FeedRelatedEntities
import io.golos.domain.mappers.EntityToModelMapper
import io.golos.domain.repositories.DiscussionsFeedRepository
import io.golos.domain.requestmodel.FeedUpdateRequest
import io.golos.domain.requestmodel.QueryResult
import io.golos.domain.use_cases.UseCase
import io.golos.domain.use_cases.model.DiscussionModel
import io.golos.domain.use_cases.model.DiscussionsFeed
import io.golos.domain.use_cases.model.FeedTimeFrameOption
import io.golos.domain.use_cases.model.UpdateOption
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-03-19.
 */
abstract class AbstractFeedUseCase<Q : FeedUpdateRequest, E : DiscussionEntity, M : DiscussionModel>
constructor(
    protected val discussionsFeedRepository: DiscussionsFeedRepository<E, Q>,
    protected val feedMapper: EntityToModelMapper<FeedRelatedEntities<E>, DiscussionsFeed<M>>,
    protected val dispatchersProvider: DispatchersProvider
) : UseCase<DiscussionsFeed<M>> {
    private val postFeedLiveData = MutableLiveData<DiscussionsFeed<M>>()
    private val lastFetchedChunkLiveData = MutableLiveData<List<M>>()

    private val feedUpdateLiveData = MutableLiveData<QueryResult<UpdateOption>>()

    private val mediatorLiveData = MediatorLiveData<Any>()
    private val observer = Observer<Any> {}

    private val useCaseScope = CoroutineScope(dispatchersProvider.uiDispatcher + SupervisorJob())

    protected abstract val baseFeedUpdateRequest: Q

    override val getAsLiveData: LiveData<DiscussionsFeed<M>> = postFeedLiveData

    val getLastFetchedChunk: LiveData<List<M>> = lastFetchedChunkLiveData

    val feedUpdateState: LiveData<QueryResult<UpdateOption>> =
        feedUpdateLiveData

    private var lastFeedJob: Job? = null

    abstract fun requestFeedUpdate(
        limit: Int = 20,
        option: UpdateOption,
        sort: DiscussionsSort? = null,
        timeFrame: FeedTimeFrameOption? = null
    )

    protected fun onFeedRelatedDataChanges() {
        lastFeedJob?.cancel()
        lastFeedJob = useCaseScope.launch {
            val feedEntity = getLastFeedData()

            if (feedEntity == null) {
                postFeedLiveData.value = DiscussionsFeed(emptyList())
                lastFetchedChunkLiveData.value = listOf()
                return@launch
            }
            //TODO empty feed state

//            val resultFeed = withContext(dispatchersProvider.calculationsDispatcher) {
//                feedMapper.map(FeedRelatedEntities(feedEntity, votes))
//            }

            val lastFeedItems = postFeedLiveData.value?.items.orEmpty()
//            val resultFeedItems = resultFeed.items

//            postFeedLiveData.value = resultFeed

//            if (feedEntity.pageId == null) lastFetchedChunkLiveData.value = resultFeedItems
//            else if (lastFeedItems.size != resultFeed.items.size) lastFetchedChunkLiveData.value =
//                resultFeedItems - lastFeedItems
        }
    }

    private fun getLastFeedData(): FeedEntity<E>? {
        val result = discussionsFeedRepository.getAsLiveData(baseFeedUpdateRequest).value
        return result
    }

    override fun subscribe() {

        mediatorLiveData.addSource(discussionsFeedRepository.getAsLiveData(baseFeedUpdateRequest))
        {
            onFeedRelatedDataChanges()
        }

        mediatorLiveData.addSource(discussionsFeedRepository.updateStates) { updatesMap ->
            val myFeedUpdatingState = updatesMap?.get(baseFeedUpdateRequest.id)

            feedUpdateLiveData.value = when (myFeedUpdatingState) {
                null -> null
                is QueryResult.Success -> QueryResult.Success(
                    myFeedUpdatingState.originalQuery.toUpdateOption()
                )
                is QueryResult.Loading -> QueryResult.Loading(
                    myFeedUpdatingState.originalQuery.toUpdateOption()
                )
                is QueryResult.Error -> QueryResult.Error(
                    myFeedUpdatingState.error,
                    myFeedUpdatingState.originalQuery.toUpdateOption()
                )
            }
        }

        mediatorLiveData.observeForever(observer)
    }

    private fun FeedUpdateRequest.toUpdateOption() =
        if (pageKey == null) UpdateOption.REFRESH_FROM_BEGINNING else UpdateOption.FETCH_NEXT_PAGE

    protected fun UpdateOption.resolveUpdateOption(): UpdateOption =
        if (this == UpdateOption.REFRESH_FROM_BEGINNING) UpdateOption.REFRESH_FROM_BEGINNING
        else if (this == UpdateOption.FETCH_NEXT_PAGE && discussionsFeedRepository.getAsLiveData(baseFeedUpdateRequest).value?.nextPageId == null) UpdateOption.REFRESH_FROM_BEGINNING
        else UpdateOption.FETCH_NEXT_PAGE


    override fun unsubscribe() {
        mediatorLiveData.removeSource(discussionsFeedRepository.getAsLiveData(baseFeedUpdateRequest))
//        mediatorLiveData.removeSource(voteRepository.updateStates)
        mediatorLiveData.removeSource(discussionsFeedRepository.updateStates)
        mediatorLiveData.removeObserver(observer)
    }
}