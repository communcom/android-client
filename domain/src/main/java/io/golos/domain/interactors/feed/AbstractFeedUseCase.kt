package io.golos.domain.interactors.feed

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import io.golos.domain.DiscussionsFeedRepository
import io.golos.domain.DispatchersProvider
import io.golos.domain.Repository
import io.golos.domain.entities.*
import io.golos.domain.interactors.UseCase
import io.golos.domain.interactors.model.DiscussionModel
import io.golos.domain.interactors.model.DiscussionsFeed
import io.golos.domain.interactors.model.FeedTimeFrameOption
import io.golos.domain.interactors.model.UpdateOption
import io.golos.domain.requestmodel.FeedUpdateRequest
import io.golos.domain.requestmodel.Identifiable
import io.golos.domain.requestmodel.QueryResult
import io.golos.domain.rules.EntityToModelMapper
import kotlinx.coroutines.*
import javax.inject.Inject

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-03-19.
 */
abstract class AbstractFeedUseCase<Q : FeedUpdateRequest, E : DiscussionEntity, M : DiscussionModel>
constructor(
    protected val discussionsFeedRepository: DiscussionsFeedRepository<E, Q>,
    protected val voteRepository: Repository<VoteRequestEntity, VoteRequestEntity>,
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
            val votes = getLastVoteData()

            if (feedEntity == null) {
                postFeedLiveData.value = DiscussionsFeed(emptyList())
                lastFetchedChunkLiveData.value = listOf()
                return@launch
            }
            //TODO empty feed state

            val resultFeed = withContext(dispatchersProvider.calculationskDispatcher) {
                feedMapper(
                    FeedRelatedEntities(
                        feedEntity,
                        votes
                    )
                )
            }

            val lastFeedItems = postFeedLiveData.value?.items.orEmpty()
            val resultFeedItems = resultFeed.items

            postFeedLiveData.value = resultFeed

            if (feedEntity.pageId == null) lastFetchedChunkLiveData.value = resultFeedItems
            else if (lastFeedItems.size != resultFeed.items.size) lastFetchedChunkLiveData.value =
                resultFeedItems - lastFeedItems
        }
    }

    private fun getLastFeedData(): FeedEntity<E>? {
        return discussionsFeedRepository.getAsLiveData(baseFeedUpdateRequest).value
    }

    private fun getLastVoteData(): Map<Identifiable.Id, QueryResult<VoteRequestEntity>> {
        return voteRepository.updateStates.value.orEmpty()
    }

    override fun subscribe() {

        mediatorLiveData.addSource(discussionsFeedRepository.getAsLiveData(baseFeedUpdateRequest))
        {
            onFeedRelatedDataChanges()
        }

        mediatorLiveData.addSource(voteRepository.updateStates)
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
        mediatorLiveData.removeSource(voteRepository.updateStates)
        mediatorLiveData.removeSource(discussionsFeedRepository.updateStates)
        mediatorLiveData.removeObserver(observer)
    }
}