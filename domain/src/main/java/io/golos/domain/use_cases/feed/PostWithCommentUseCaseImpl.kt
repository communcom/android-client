package io.golos.domain.use_cases.feed

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import io.golos.domain.repositories.DiscussionsFeedRepository
import io.golos.domain.DispatchersProvider
import io.golos.domain.repositories.Repository
import io.golos.domain.dto.*
import io.golos.domain.extensions.distinctUntilChanged
import io.golos.domain.use_cases.model.DiscussionIdModel
import io.golos.domain.use_cases.model.PostModel
import io.golos.domain.mappers.CommentsFeedEntityToModelMapper
import io.golos.domain.mappers.PostEntitiesToModelMapper
import io.golos.domain.requestmodel.CommentFeedUpdateRequest
import io.golos.domain.requestmodel.PostFeedUpdateRequest
import kotlinx.coroutines.*
import javax.inject.Inject

interface PostWithCommentUseCase {
    val getPostAsLiveData: LiveData<PostModel>

    fun subscribe()

    fun unsubscribe()
}

class PostWithCommentUseCaseImpl
@Inject
constructor (
    postId: DiscussionIdModel,
    private val postFeedRepository: DiscussionsFeedRepository<PostEntity, PostFeedUpdateRequest>,
    private val toModelMapper: PostEntitiesToModelMapper,
    commentsFeedRepository: DiscussionsFeedRepository<CommentEntity, CommentFeedUpdateRequest>,
    voteRepository: Repository<VoteRequestEntity, VoteRequestEntity>,
    feedMapper: CommentsFeedEntityToModelMapper,
    dispatchersProvider: DispatchersProvider
) : PostCommentsFeedUseCase(
    postId,
    commentsFeedRepository,
    voteRepository,
    feedMapper,
    dispatchersProvider
), PostWithCommentUseCase {

    private val useCaseScope = CoroutineScope(dispatchersProvider.uiDispatcher + SupervisorJob())
    private var job: Job? = null
    private val observer = Observer<Any> {}
    private val mediator = MediatorLiveData<Any>()

    private val postLiveData = MutableLiveData<PostModel>()

    override val getPostAsLiveData: LiveData<PostModel> = postLiveData.distinctUntilChanged()

    private fun onRelatedDataChanges() {
        job?.cancel()
        job = useCaseScope.launch {
            val postEntity = getPostEntity() ?: return@launch
            val votes = getVotes()

            Log.d("UPDATE_POST", "PostWithCommentUseCaseImpl content: ${postEntity.content.body.postBlock}")

            postLiveData.value = withContext(dispatchersProvider.calculationsDispatcher) {
                val mappedPost = toModelMapper.map(
                    DiscussionRelatedEntities(
                        postEntity,
                        votes.orEmpty().values.find { it.originalQuery.discussionIdEntity.asModel == postId })
                )

                Log.d("UPDATE_POST", "PostWithCommentUseCaseImpl mapped: ${mappedPost.content.body.postBlock}")

                mappedPost
            }
        }
    }


    override fun subscribe() {
        Log.d("UPDATE_POST", "PostWithCommentUseCaseImpl subscribe()")

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

    private fun getPostEntity() = postFeedRepository.getDiscussionAsLiveData(DiscussionIdEntity.fromModel(postId)).value

    private fun getVotes() = voteRepository.updateStates.value

    override fun unsubscribe() {
        Log.d("UPDATE_POST", "PostWithCommentUseCaseImpl unsubscribe()")

        super.unsubscribe()
        mediator.removeSource(postFeedRepository.getDiscussionAsLiveData(DiscussionIdEntity.fromModel(postId)))
        mediator.removeSource(voteRepository.updateStates)
        mediator.removeObserver(observer)
    }
}