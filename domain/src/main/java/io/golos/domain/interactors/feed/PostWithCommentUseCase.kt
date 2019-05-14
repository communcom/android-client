package io.golos.domain.interactors.feed

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import io.golos.domain.DiscussionsFeedRepository
import io.golos.domain.DispatchersProvider
import io.golos.domain.Repository
import io.golos.domain.distinctUntilChanged
import io.golos.domain.entities.*
import io.golos.domain.interactors.model.CommentModel
import io.golos.domain.interactors.model.DiscussionIdModel
import io.golos.domain.interactors.model.DiscussionsFeed
import io.golos.domain.interactors.model.PostModel
import io.golos.domain.requestmodel.CommentFeedUpdateRequest
import io.golos.domain.requestmodel.PostFeedUpdateRequest
import io.golos.domain.rules.EntityToModelMapper
import kotlinx.coroutines.*

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-03-27.
 */
class PostWithCommentUseCase(
    postId: DiscussionIdModel,
    private val postFeedRepository: DiscussionsFeedRepository<PostEntity, PostFeedUpdateRequest>,
    private val toModelMapper: EntityToModelMapper<DiscussionRelatedEntities<PostEntity>, PostModel>,
    commentsFeedRepository: DiscussionsFeedRepository<CommentEntity, CommentFeedUpdateRequest>,
    voteRepository: Repository<VoteRequestEntity, VoteRequestEntity>,
    feedMapper: EntityToModelMapper<FeedRelatedEntities<CommentEntity>, DiscussionsFeed<CommentModel>>,
    dispatchersProvider: DispatchersProvider
) : PostCommentsFeedUseCase(
    postId,
    commentsFeedRepository,
    voteRepository,
    feedMapper,
    dispatchersProvider
) {

    private val useCaseScope = CoroutineScope(dispatchersProvider.uiDispatcher + SupervisorJob())
    private var job: Job? = null
    private val observer = Observer<Any> {}
    private val mediator = MediatorLiveData<Any>()

    private val postLiveData = MutableLiveData<PostModel>()

    val getPostAsLiveData = postLiveData.distinctUntilChanged()

    private fun onRelatedDataChanges() {
        job?.cancel()
        job = useCaseScope.launch {
            val postEntity = getPostEntity() ?: return@launch
            val votes = getVotes()

            postLiveData.value = withContext(dispatchersProvider.workDispatcher) {
                toModelMapper(
                    DiscussionRelatedEntities(
                        postEntity,
                        votes.orEmpty().values.find { it.originalQuery.discussionIdEntity.asModel == postId })
                )
            }
        }
    }


    override fun subscribe() {
        super.subscribe()
        mediator.addSource(postFeedRepository.getDiscussionAsLiveData(DiscussionIdEntity.fromModel(postId))) {
            onRelatedDataChanges()
        }
        mediator.addSource(voteRepository.updateStates) {
            onRelatedDataChanges()
        }
        mediator.observeForever(observer)
        postFeedRepository.requestDiscussionUpdate(DiscussionIdEntity.fromModel(postId))
    }

    private fun getPostEntity() =
        postFeedRepository.getDiscussionAsLiveData(DiscussionIdEntity.fromModel(postId)).value

    private fun getVotes() = voteRepository.updateStates.value

    override fun unsubscribe() {
        super.unsubscribe()
        mediator.removeSource(postFeedRepository.getDiscussionAsLiveData(DiscussionIdEntity.fromModel(postId)))
        mediator.removeSource(voteRepository.updateStates)
        mediator.removeObserver(observer)
    }
}