package io.golos.domain.interactors.feed

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import io.golos.domain.DiscussionsFeedRepository
import io.golos.domain.DispatchersProvider
import io.golos.domain.Repository
import io.golos.domain.distinctUntilChanged
import io.golos.domain.entities.FeedEntity
import io.golos.domain.entities.FeedRelatedEntities
import io.golos.domain.entities.PostEntity
import io.golos.domain.entities.VoteRequestEntity
import io.golos.domain.interactors.UseCase
import io.golos.domain.interactors.model.PostFeed
import io.golos.domain.interactors.model.PostModel
import io.golos.domain.interactors.model.UpdateOption
import io.golos.domain.model.Identifiable
import io.golos.domain.model.PostFeedUpdateRequest
import io.golos.domain.model.QueryResult
import io.golos.domain.rules.EntityToModelMapper
import kotlinx.coroutines.*

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-03-19.
 */
abstract class AbstractFeedUseCase<Q : PostFeedUpdateRequest>(
    protected val postFeedRepository: DiscussionsFeedRepository<PostEntity, PostFeedUpdateRequest>,
    private val voteRepository: Repository<VoteRequestEntity, VoteRequestEntity>,
    protected val feedMapper: EntityToModelMapper<FeedRelatedEntities, PostFeed>,
    private val dispatchersProvider: DispatchersProvider
) : UseCase<PostFeed> {
    private val postFeedLiveData = MutableLiveData<PostFeed>()
    private val lastFetchedChunkLiveData = MutableLiveData<List<PostModel>>()

    private val feedUpdateLiveData = MutableLiveData<io.golos.domain.model.QueryResult<UpdateOption>>()

    private val mediatorLiveData = MediatorLiveData<Any>()
    private val observer = Observer<Any> {}

    private val useCaseScope = CoroutineScope(dispatchersProvider.uiDispatcher + SupervisorJob())

    protected abstract val baseFeedUpdateRequest: Q

    override val getAsLiveData: LiveData<PostFeed> = postFeedLiveData.distinctUntilChanged()

    val getLastFetchedChunk: LiveData<List<PostModel>> = lastFetchedChunkLiveData.distinctUntilChanged()

    val feedUpdateState: LiveData<io.golos.domain.model.QueryResult<UpdateOption>> =
        feedUpdateLiveData.distinctUntilChanged()

    private var lastFeedJob: Job? = null

    abstract fun requestFeedUpdate(
        limit: Int = 20,
        option: UpdateOption
    )

    protected fun onFeedRelatedDataChanges() {
        lastFeedJob?.cancel()
        lastFeedJob = useCaseScope.launch {
            val feedEntity = getLastFeedData()
            val votes = getLastVoteData()

            if (feedEntity == null) {
                postFeedLiveData.value = PostFeed(emptyList())
                lastFetchedChunkLiveData.value = listOf()
                return@launch
            }
            //TODO empty feed state

            val resultFeed = withContext(dispatchersProvider.workDispatcher) {
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

    private fun getLastFeedData(): FeedEntity<PostEntity>? {
        return postFeedRepository.getAsLiveData(baseFeedUpdateRequest).value
    }

    private fun getLastVoteData(): Map<Identifiable.Id, QueryResult<VoteRequestEntity>> {
        return voteRepository.updateStates.value.orEmpty()
    }

    override fun subscribe() {

        mediatorLiveData.addSource(postFeedRepository.getAsLiveData(baseFeedUpdateRequest))
        {
            onFeedRelatedDataChanges()
        }

        mediatorLiveData.addSource(voteRepository.updateStates)
        {
            onFeedRelatedDataChanges()
        }

        mediatorLiveData.addSource(postFeedRepository.updateStates) { updatesMap ->
            val myFeedUpdatingState = updatesMap?.get(baseFeedUpdateRequest.id)

            feedUpdateLiveData.value = when (myFeedUpdatingState) {
                null -> null
                is io.golos.domain.model.QueryResult.Success -> io.golos.domain.model.QueryResult.Success(
                    myFeedUpdatingState.originalQuery.toUpdateOption()
                )
                is io.golos.domain.model.QueryResult.Loading -> io.golos.domain.model.QueryResult.Loading(
                    myFeedUpdatingState.originalQuery.toUpdateOption()
                )
                is io.golos.domain.model.QueryResult.Error -> io.golos.domain.model.QueryResult.Error(
                    myFeedUpdatingState.error,
                    myFeedUpdatingState.originalQuery.toUpdateOption()
                )
            }
        }

        mediatorLiveData.observeForever(observer)
    }

    private fun PostFeedUpdateRequest.toUpdateOption() =
        if (pageKey == null) UpdateOption.REFRESH_FROM_BEGINNING else UpdateOption.FETCH_NEXT_PAGE

    protected fun UpdateOption.resolveUpdateOption(): UpdateOption =
        if (this == UpdateOption.REFRESH_FROM_BEGINNING) UpdateOption.REFRESH_FROM_BEGINNING
        else if (this == UpdateOption.FETCH_NEXT_PAGE && postFeedRepository.getAsLiveData(baseFeedUpdateRequest).value?.nextPageId == null) UpdateOption.REFRESH_FROM_BEGINNING
        else UpdateOption.FETCH_NEXT_PAGE


    override fun unsubscribe() {
        mediatorLiveData.removeSource(postFeedRepository.getAsLiveData(baseFeedUpdateRequest))
        mediatorLiveData.removeSource(voteRepository.updateStates)
        mediatorLiveData.removeSource(postFeedRepository.updateStates)
        mediatorLiveData.removeObserver(observer)
    }
}