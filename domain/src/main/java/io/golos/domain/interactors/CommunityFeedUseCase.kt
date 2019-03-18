package io.golos.domain.interactors

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import io.golos.domain.DiscussionsFeedRepository
import io.golos.domain.DispatchersProvider
import io.golos.domain.entities.DiscussionsSort
import io.golos.domain.entities.FeedEntity
import io.golos.domain.entities.PostEntity
import io.golos.domain.interactors.model.CommunityId
import io.golos.domain.interactors.model.PostFeed
import io.golos.domain.interactors.model.UpdateOption
import io.golos.domain.model.CommunityFeedUpdateRequest
import io.golos.domain.model.PostFeedUpdateRequest
import io.golos.domain.rules.EntityToModelMapper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-03-18.
 */
class CommunityFeedUseCase(
    val community: CommunityId,
    private val postFeedRepository: DiscussionsFeedRepository<PostEntity, PostFeedUpdateRequest>,
    private val feedMapper: EntityToModelMapper<FeedEntity<PostEntity>, PostFeed>,
    private val dispatchersProvider: DispatchersProvider
) : UseCase<PostFeed> {
    private val postFeedLiveData = MutableLiveData<PostFeed>()
    private val mediatorLiveData = MediatorLiveData<Any>()
    private val observer = Observer<Any> {}

    private val useCaseScope = CoroutineScope(dispatchersProvider.uiDispatcher + SupervisorJob())

    private val basicRequest = CommunityFeedUpdateRequest(
        community.id,
        0,
        DiscussionsSort.FROM_NEW_TO_OLD, null
    )

    override val getAsLiveData: LiveData<PostFeed>
        get() = postFeedLiveData

    fun requestFeedUpdate(
        limit: Int = 20,
        option: UpdateOption
    ) {
        postFeedRepository.makeAction(
            CommunityFeedUpdateRequest(
                community.id,
                limit,
                DiscussionsSort.FROM_NEW_TO_OLD,
                when (option) {
                    UpdateOption.REFRESH_FROM_BEGINNING -> null
                    UpdateOption.FETCH_NEXT_PAGE -> postFeedRepository.getAsLiveData(basicRequest).value?.nextPageId
                }
            )
        )
    }

    fun subscribe() {
        mediatorLiveData.observeForever(observer)
        mediatorLiveData.addSource(
            postFeedRepository.getAsLiveData(basicRequest)
        ) { feedEntity: FeedEntity<PostEntity>? ->
            print("on new entity")
            if (feedEntity == null) postFeedLiveData.value = PostFeed(emptyList())
            //TODO empty feed state
            else useCaseScope.launch {
                val resultFeed = withContext(dispatchersProvider.workDispatcher) { feedMapper(feedEntity) }
                postFeedLiveData.value = resultFeed
            }
        }

    }

    fun unsubscribe() {
        mediatorLiveData.removeSource(postFeedRepository.getAsLiveData(basicRequest))
        mediatorLiveData.removeObserver(observer)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CommunityFeedUseCase

        if (community != other.community) return false

        return true
    }

    override fun hashCode(): Int {
        return community.hashCode()
    }

    override fun toString(): String {
        return "CommunityFeedUseCase(community=$community)"
    }
}