package io.golos.data.repositories

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.golos.commun4j.model.CyberDiscussionRaw
import io.golos.commun4j.model.GetDiscussionsResultRaw
import io.golos.data.errors.CyberServicesError
import io.golos.data.putIfAbsentAndGet
import io.golos.data.replaceByProducer
import io.golos.domain.repositories.DiscussionsFeedRepository
import io.golos.domain.DispatchersProvider
import io.golos.domain.Entity
import io.golos.domain.commun_entities.PostDiscussionRaw
import io.golos.domain.entities.*
import io.golos.domain.mappers.CommunToEntityMapper
import io.golos.domain.requestmodel.FeedUpdateRequest
import io.golos.domain.requestmodel.Identifiable
import io.golos.domain.requestmodel.QueryResult
import io.golos.domain.rules.EmptyEntityProducer
import io.golos.domain.rules.EntityMerger
import io.golos.domain.rules.FeedUpdateRequestsWithResult
import io.golos.domain.rules.RequestApprover
import kotlinx.coroutines.*
import timber.log.Timber
import java.util.*
import kotlin.collections.HashMap

/**
 * Created by yuri yurivladdurain@gmail.com on 11/03/2019.
 */

abstract class AbstractDiscussionsRepository<D : DiscussionEntity, Q : FeedUpdateRequest>(
    private val feedMapper: CommunToEntityMapper<FeedUpdateRequestsWithResult<FeedUpdateRequest>, FeedEntity<D>>,
    private val discussionMapper: CommunToEntityMapper<PostDiscussionRaw, D>?,
    private val discussionMerger: EntityMerger<D>,
    private val discussionsFeedMerger: EntityMerger<FeedRelatedData<D>>,
    private val requestApprover: RequestApprover<Q>,
    private val emptyFeedProducer: EmptyEntityProducer<FeedEntity<D>>,
    private val dispatchersProvider: DispatchersProvider
) : DiscussionsFeedRepository<D, Q> {

    protected val discussionsFeedMap: MutableMap<Identifiable.Id, MutableLiveData<FeedEntity<D>>> = hashMapOf()

    private var discussionLiveData: MutableLiveData<D> = MutableLiveData()
    private var activeUpdatingPost: DiscussionIdEntity? = null

    private val feedsUpdatingStatesMap: MutableLiveData<Map<Identifiable.Id, QueryResult<Q>>> = MutableLiveData()

    private val repositoryScope = CoroutineScope(dispatchersProvider.uiDispatcher + SupervisorJob())

    private val postJobMap = Collections.synchronizedMap(HashMap<DiscussionIdEntity, Job>())
    private val feedJobMap = Collections.synchronizedMap(HashMap<Identifiable.Id, Job>())

    protected val fixedDiscussions: MutableMap<Identifiable.Id, Set<D>> =
        Collections.synchronizedMap(HashMap<Identifiable.Id, Set<D>>())

    override val updateStates: LiveData<Map<Identifiable.Id, QueryResult<Q>>>
        get() = this.feedsUpdatingStatesMap


    override fun getAsLiveData(params: Q): LiveData<FeedEntity<D>> {
        if (params == allDataRequest) {
            return MutableLiveData<FeedEntity<D>>()
                .apply {

                    val additionalItem =
                        discussionLiveData.value?.let { listOf(it) } ?: emptyFeedProducer.invoke().discussions

                    value = getAllPostsAsLiveDataList().mapNotNull { it.value }
                        .fold(FeedEntity(additionalItem, null, "stub")) { collector, item ->
                            FeedEntity(collector.discussions + item.discussions, null, "stub")
                        }
                }
        }
        return discussionsFeedMap.putIfAbsentAndGet(params.id)
    }

    override fun getDiscussionAsLiveData(discussionIdEntity: DiscussionIdEntity): LiveData<D> {

        if (activeUpdatingPost == discussionIdEntity) return discussionLiveData

        if (discussionLiveData.value?.contentId == discussionIdEntity) return discussionLiveData

        discussionLiveData = MutableLiveData()

        discussionLiveData.value = getAllPostsAsLiveDataList()
            .mapNotNull { it.value?.discussions }
            .flatten()
            .findLast { it.contentId == discussionIdEntity }

        Log.d("UPDATE_POST", "AbstractDiscussionsRepository::getDiscussionAsLiveData content: ${(discussionLiveData.value as? PostEntity)?.content?.body?.postBlock?.content}")

        if (discussionLiveData.value == null) {
            requestDiscussionUpdate(discussionIdEntity)
        }

        return discussionLiveData
    }

    override fun requestDiscussionUpdate(updatingDiscussionId: DiscussionIdEntity) {
        postJobMap[updatingDiscussionId]?.cancel()

        activeUpdatingPost = updatingDiscussionId

        launch(exceptionCallback = {
            activeUpdatingPost = null
        }) {

            val updatedPost = try {
                //try to get updated post
                getOnBackground { getDiscussionItem(updatingDiscussionId) }
            } catch (e: CyberServicesError) {
                Timber.e(e)
                //if post with this id is not found then we should
                //delete it from all the LiveData's
                if (e.message?.contains("404") == true) {
                    removePost(updatingDiscussionId)
                    activeUpdatingPost = null
                    return@launch
                }
                //else just rethrow this error
                else throw e
            }

            Log.d("UPDATE_POST", "AbstractDiscussionsRepository::requestDiscussionUpdate content: ${updatedPost.content}")

            throw UnsupportedOperationException("")



/*
            val updatedPostEntity =   discussionMapper.convertOnBackground(updatedPost)

            Log.d("UPDATE_POST", "AbstractDiscussionsRepository::requestDiscussionUpdate mapped: ${(updatedPostEntity as? PostEntity)?.content?.body?.postBlock}")

            var mergedEntity: D? = null

            getAllPostsAsLiveDataList()
                .forEach { feedLiveData ->
                    val feed = feedLiveData.value ?: return@forEach
                    val posts = feed.discussions

                    if (posts.any { it.contentId == updatingDiscussionId }) {
                        val postWithReplacedDiscussion = posts
                            .replaceByProducer({ postEntity -> postEntity.contentId == updatedPostEntity.contentId },
                                { postEntity ->
                                    val result = discussionMerger(updatedPostEntity, postEntity)
                                    mergedEntity = result
                                    result
                                })

                        feedLiveData.value = feedLiveData.value?.copy(postWithReplacedDiscussion)
                    }
                }

            Log.d("UPDATE_POST", "AbstractDiscussionsRepository::requestDiscussionUpdate merged: ${mergedEntity?.javaClass?.simpleName}; $mergedEntity")

            if (discussionLiveData.value?.contentId == updatedPostEntity.contentId ||
                activeUpdatingPost == updatingDiscussionId
            ) discussionLiveData.value = mergedEntity ?: updatedPostEntity

            activeUpdatingPost = null
*/

        }.let { job -> postJobMap[updatingDiscussionId] = job }
    }

    private fun removePost(updatingDiscussionId: DiscussionIdEntity) {
        getAllPostsAsLiveDataList()
            .forEach { feedLiveData ->
                val feed = feedLiveData.value ?: return@forEach
                val posts = feed.discussions

                if (posts.any { it.contentId == updatingDiscussionId }) {
                    val filteredPosts = posts
                        .filter { it.contentId != updatingDiscussionId }

                    feedLiveData.value = feedLiveData.value?.copy(filteredPosts)
                }
            }
    }

    override fun onAuthorMetadataUpdated(metadataEntity: UserMetadataEntity) {
        getAllPostsAsLiveDataList()
            .forEach { feedLiveData ->
                val feed = feedLiveData.value ?: return@forEach
                val posts = feed.discussions

                val updatedPosts = posts.map {
                    if (it.contentId.userId == metadataEntity.userId.name) {
                        it.author.avatarUrl = metadataEntity.personal.avatarUrl ?: ""
                    }
                    it
                }

                feedLiveData.value = feedLiveData.value?.copy(updatedPosts)
            }
    }

    //update feed
    override fun makeAction(params: Q) {
        launch(exceptionCallback = {
            feedsUpdatingStatesMap.value = feedsUpdatingStatesMap.value.orEmpty() + (params.id to QueryResult.Error(it, params))
        }) {
            if (!requestApprover.approve(params)) return@launch

            discussionsFeedMap.putIfAbsentAndGet(params.id)

            feedsUpdatingStatesMap.value =
                feedsUpdatingStatesMap.value.orEmpty() + (params.id to QueryResult.Loading(params))

            val feed = getOnBackground { getFeedOnBackground(params) }

            val feedEntity = feedMapper.map(FeedUpdateRequestsWithResult(params, feed))

            val oldFeed = discussionsFeedMap[params.id]?.value ?: emptyFeedProducer()

            val fixedEntities = fixedDiscussions[params.id] ?: hashSetOf()

            val resultingFeed =
                discussionsFeedMerger(FeedRelatedData(feedEntity, fixedEntities), FeedRelatedData(oldFeed, hashSetOf()))

            fixedDiscussions[params.id] = resultingFeed.fixedPositionEntities
            discussionsFeedMap[params.id]?.value = resultingFeed.feed

            feedsUpdatingStatesMap.value =
                feedsUpdatingStatesMap.value.orEmpty() + (params.id to QueryResult.Success(params))

        }.let { job ->
            feedJobMap[params.id]?.cancel()
            feedJobMap[params.id] = job
        }
    }

    private fun <T> async(
        start: CoroutineStart = CoroutineStart.DEFAULT,
        block: suspend CoroutineScope.() -> T
    ) = repositoryScope.async(dispatchersProvider.calculationsDispatcher, start, block)

    private suspend fun <T> getOnBackground(
        block: suspend CoroutineScope.() -> T
    ) = withContext(dispatchersProvider.calculationsDispatcher, block)

    private fun launch(
        exceptionCallback: (Exception) -> Unit = {},
        start: CoroutineStart = CoroutineStart.DEFAULT,
        block: suspend CoroutineScope.() -> Unit
    ) = repositoryScope.launch(start = start) {
        try {
            block()
        } catch (e: java.lang.Exception) {
            exceptionCallback(e)
            Timber.e(e)
        }
    }

    protected abstract suspend fun getDiscussionItem(params: DiscussionIdEntity): CyberDiscussionRaw

    protected abstract suspend fun getFeedOnBackground(updateRequest: Q): GetDiscussionsResultRaw


    private suspend fun <F, T : Entity> CommunToEntityMapper<F, T>.convertOnBackground(cyberItem: F) =
        getOnBackground { map(cyberItem) }

    private fun getAllPostsAsLiveDataList() = discussionsFeedMap.values.toList()
}





