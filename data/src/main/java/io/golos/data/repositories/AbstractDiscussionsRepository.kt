package io.golos.data.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.golos.cyber4j.model.CyberDiscussion
import io.golos.cyber4j.model.DiscussionsResult
import io.golos.data.putIfAbsentAndGet
import io.golos.data.replaceByProducer
import io.golos.domain.DiscussionsFeedRepository
import io.golos.domain.Entity
import io.golos.domain.Logger
import io.golos.domain.entities.DiscussionEntity
import io.golos.domain.entities.DiscussionIdEntity
import io.golos.domain.entities.FeedEntity
import io.golos.domain.model.FeedUpdateRequest
import io.golos.domain.model.Identifiable
import io.golos.domain.model.Result
import io.golos.domain.rules.*
import kotlinx.coroutines.*
import java.util.*
import kotlin.collections.HashMap

/**
 * Created by yuri yurivladdurain@gmail.com on 11/03/2019.
 */

abstract class AbstractDiscussionsRepository<D : DiscussionEntity, Q : FeedUpdateRequest>(
    private val feedMapper: CyberToEntityMapper<FeedUpdateRequestsWithResult<FeedUpdateRequest>, FeedEntity<D>>,
    private val postMapper: CyberToEntityMapper<CyberDiscussion, D>,
    private val postMerger: EntityMerger<D>,
    private val feedMerger: EntityMerger<FeedEntity<D>>,
    private val emptyFeedProducer: EmptyEntityProducer<FeedEntity<D>>,
    mainDispatcher: CoroutineDispatcher,
    private val workerDispatcher: CoroutineDispatcher,
    private val logger: Logger
) : DiscussionsFeedRepository<D, Q> {

    private val discussionsFeedMap: MutableMap<Identifiable.Id, MutableLiveData<FeedEntity<D>>> = hashMapOf()
    private val feedsUpdatingStatesMap: MutableLiveData<Map<Identifiable.Id, Result<Q>>> = MutableLiveData()

    private val repositoryScope = CoroutineScope(mainDispatcher + SupervisorJob())

    private val postJobMap = Collections.synchronizedMap(HashMap<DiscussionIdEntity, Job>())
    private val feedJobMap = Collections.synchronizedMap(HashMap<Identifiable.Id, Job>())

    override val updateStates: LiveData<Map<Identifiable.Id, Result<Q>>>
        get() = this.feedsUpdatingStatesMap

    override fun getAsLiveData(params: Q): LiveData<FeedEntity<D>> {
        return discussionsFeedMap.putIfAbsentAndGet(params.id)
    }

    override fun requestDiscussionUpdate(updatingDiscussionId: DiscussionIdEntity) {
        postJobMap[updatingDiscussionId]?.cancel()

        launch {

            val updatedPost = getOnBackground { getDiscussionItem(updatingDiscussionId) }

            val updatedPostEntity = postMapper.convertOnBackground(updatedPost)

            getAllPostsAsLiveDataList()
                .forEach { feedLiveData ->
                    val feed = feedLiveData.value ?: return@forEach
                    val posts = feed.discussions

                    if (posts.any { it.contentId == updatingDiscussionId }) {
                        val postWithReplacedDiscussion = posts
                            .replaceByProducer({ postEntity -> postEntity.contentId == updatedPostEntity.contentId },
                                { postEntity -> postMerger(updatedPostEntity, postEntity) })

                        feedLiveData.value = feedLiveData.value?.copy(postWithReplacedDiscussion)
                    }
                }
        }.let { job -> postJobMap[updatingDiscussionId] = job }
    }

    //update feed
    override fun makeAction(params: Q) {
        launch {

            discussionsFeedMap.putIfAbsentAndGet(params.id)

            feedJobMap[params.id]?.cancel()

            feedsUpdatingStatesMap.value =
                feedsUpdatingStatesMap.value.orEmpty() + (params.id to Result.Loading(params))

            val feed = getOnBackground { getFeedOnBackground(params) }

            val feedEntity =  feedMapper.invoke(FeedUpdateRequestsWithResult(params, feed))

            val oldFeed = discussionsFeedMap[params.id]?.value ?: emptyFeedProducer()

            val resultingFeed = feedMerger(feedEntity, oldFeed)

            discussionsFeedMap[params.id]?.value = resultingFeed

            feedsUpdatingStatesMap.value =
                feedsUpdatingStatesMap.value.orEmpty() + (params.id to Result.Success(params))

        }.let { job -> feedJobMap[params.id] = job }
    }

    private fun <T> async(
        start: CoroutineStart = CoroutineStart.DEFAULT,
        block: suspend CoroutineScope.() -> T
    ) = repositoryScope.async(workerDispatcher, start, block)

    private suspend fun <T> getOnBackground(
        block: suspend CoroutineScope.() -> T
    ) = kotlinx.coroutines.withContext(workerDispatcher, block)

    private fun launch(
        exceptionCallback: (Exception) -> Unit = {},
        start: CoroutineStart = CoroutineStart.DEFAULT,
        block: suspend CoroutineScope.() -> Unit
    ) = repositoryScope.launch(start = start) {
        try {
            block()
        } catch (e: java.lang.Exception) {
            logger(e)
        }
    }

    protected abstract suspend fun getDiscussionItem(params: DiscussionIdEntity): CyberDiscussion

    protected abstract suspend fun getFeedOnBackground(updateRequest: Q): DiscussionsResult


    private suspend fun <F, T : Entity> CyberToEntityMapper<F, T>.convertOnBackground(cyberItem: F) =
        getOnBackground { invoke(cyberItem) }

    private fun getAllPostsAsLiveDataList() = discussionsFeedMap.values.toList()
}





