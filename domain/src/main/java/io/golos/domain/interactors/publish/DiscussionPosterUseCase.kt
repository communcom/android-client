package io.golos.domain.interactors.publish

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import io.golos.domain.DispatchersProvider
import io.golos.domain.FromSpannedToHtmlTransformer
import io.golos.domain.commun_entities.CommunityId
import io.golos.domain.repositories.Repository
import io.golos.domain.entities.*
import io.golos.domain.interactors.UseCase
import io.golos.domain.interactors.model.*
import io.golos.domain.extensions.map
import io.golos.domain.requestmodel.*
import kotlinx.coroutines.*
import javax.inject.Inject

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-04-02.
 */
class DiscussionPosterUseCase
@Inject
constructor(
    private val discussionCreationRepository: Repository<DiscussionCreationResultEntity, DiscussionCreationRequestEntity>,
    dispatchersProvider: DispatchersProvider,
    private val fromSpannableTransformer: FromSpannedToHtmlTransformer
) : UseCase<QueryResult<DiscussionCreationResultModel>> {

    private val parentJob = SupervisorJob()

    private val repositoryScope = CoroutineScope(dispatchersProvider.uiDispatcher + parentJob)
    private var lastJob: Job? = null

    private val observer = Observer<Any> {}
    private val mediator = MediatorLiveData<Any>()

    private val lastPostCreationModel = MutableLiveData<QueryResult<DiscussionCreationResultModel>>()

    private var lastPostCreationRequest: DiscussionCreationRequestEntity? = null

    override val getAsLiveData: LiveData<QueryResult<DiscussionCreationResultModel>>
        get() = lastPostCreationModel

    override fun subscribe() {
        super.subscribe()
        mediator.observeForever(observer)
        mediator.addSource(discussionCreationRepository.updateStates) {
            onRelatedDataChanged()
        }
    }

    override fun unsubscribe() {
        super.unsubscribe()
        mediator.removeObserver(observer)
        mediator.removeSource(discussionCreationRepository.updateStates)
        parentJob.cancel()
    }

    private fun onRelatedDataChanged() {
        lastJob?.cancel()

        lastJob = repositoryScope.launch {
            delay(30)

            val lastPostCreationEntity = lastPostCreationRequest ?: return@launch

            val createdPostLiveData = discussionCreationRepository.getAsLiveData(lastPostCreationEntity).value
            val updateStates = discussionCreationRepository.updateStates.value.orEmpty()
            val lastPostUpdateState = updateStates[lastPostCreationEntity.id] ?: return@launch

            lastPostCreationModel.value = when (lastPostUpdateState) {
                is QueryResult.Loading -> lastPostUpdateState.map(lastPostCreationEntity.toEmptyModel())

                is QueryResult.Error -> {
                    lastPostUpdateState.map(
                        lastPostCreationEntity.toEmptyModel()
                    )
                }
                is QueryResult.Success -> {
                    when (createdPostLiveData) {
                        null -> QueryResult.Error(
                            IllegalStateException(
                                "for some reason," +
                                        "successful discussion state corresponds with null result in repository"
                            ),
                            lastPostCreationEntity.toEmptyModel()
                        )
                        is PostCreationResultEntity -> QueryResult.Success<DiscussionCreationResultModel>(
                            PostCreationResultModel(
                                DiscussionIdModel(
                                    createdPostLiveData.postId.userId, createdPostLiveData.postId.permlink
                                )
                            )
                        )
                        is CommentCreationResultEntity -> QueryResult.Success<DiscussionCreationResultModel>(
                            CommentCreationResultModel(
                                DiscussionIdModel(
                                    createdPostLiveData.commentId.userId, createdPostLiveData.commentId.permlink
                                ),
                                DiscussionIdModel(
                                    createdPostLiveData.parentId.userId, createdPostLiveData.parentId.permlink
                                )
                            )
                        )
                        is UpdatePostResultEntity -> QueryResult.Success<DiscussionCreationResultModel>(
                            UpdatePostResultModel(
                                DiscussionIdModel(createdPostLiveData.id.userId, createdPostLiveData.id.permlink)
                            )
                        )
                        is DeleteDiscussionResultEntity -> QueryResult.Success<DiscussionCreationResultModel>(
                            DeleteDiscussionResultModel(
                                DiscussionIdModel(createdPostLiveData.id.userId, createdPostLiveData.id.permlink)
                            )
                        )
                    }
                }
            }

            if (lastPostUpdateState !is QueryResult.Loading)
                lastPostCreationRequest = null
        }
    }

    fun deletePostOrComment(discussionId: DiscussionIdModel) {
        val request = DeleteDiscussionRequestEntity(discussionId.permlink)
        lastPostCreationRequest = request
        discussionCreationRepository.makeAction(request)
    }

    fun createPostOrComment(request: DiscussionCreationRequest) {

        val requestEntity = when (request) {
            is PostCreationRequestModel -> PostCreationRequestEntity(
                request.title,
                request.body.toString(),
                request.body,
                request.tags,
                CommunityId(""),
                request.images
            )
            is CommentCreationRequestModel -> CommentCreationRequestEntity(
                request.body,
                request.parentId,
                request.tags
            )
            else -> throw UnsupportedOperationException()
        }
        lastPostCreationRequest = requestEntity
        discussionCreationRepository.makeAction(requestEntity)
    }

    fun updatePost(request: UpdatePostRequestModel) {
        val requestEntity = PostUpdateRequestEntity(
            request.permlink,
            request.title,
            fromSpannableTransformer.transform(request.body),
            request.body,
            request.tags,
            request.images
        )

        lastPostCreationRequest = requestEntity
        discussionCreationRepository.makeAction(requestEntity)
    }

    private fun DiscussionCreationRequestEntity.toEmptyModel() =
        if (this is PostCreationRequestEntity) PostCreationResultModel.empty
        else CommentCreationResultModel.empty
}