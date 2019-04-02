package io.golos.domain.interactors.publish

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import io.golos.domain.DispatchersProvider
import io.golos.domain.Repository
import io.golos.domain.entities.CommentCreationResultEntity
import io.golos.domain.entities.DiscussionCreationResultEntity
import io.golos.domain.entities.PostCreationResultEntity
import io.golos.domain.interactors.UseCase
import io.golos.domain.interactors.model.*
import io.golos.domain.map
import io.golos.domain.model.CommentCreationRequestEntity
import io.golos.domain.model.DiscussionCreationRequestEntity
import io.golos.domain.model.PostCreationRequestEntity
import io.golos.domain.model.QueryResult
import kotlinx.coroutines.*

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-04-02.
 */
class DiscussionPosterUseCase(
    private val discussionCreationRepository: Repository<DiscussionCreationResultEntity, DiscussionCreationRequestEntity>,
    private val dispatchersProvider: DispatchersProvider
) :
    UseCase<QueryResult<DiscussionCreationResultModel>> {

    private val repositoryScope = CoroutineScope(dispatchersProvider.uiDispatcher + SupervisorJob())
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

                is QueryResult.Error -> lastPostUpdateState.map(
                    lastPostCreationEntity.toEmptyModel()
                )
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
                                    createdPostLiveData.postId.userId, createdPostLiveData.postId.permlink,
                                    createdPostLiveData.postId.refBlockNum
                                )
                            )
                        )
                        is CommentCreationResultEntity -> QueryResult.Success<DiscussionCreationResultModel>(
                            CommentCreationResultModel(
                                DiscussionIdModel(
                                    createdPostLiveData.commentId.userId, createdPostLiveData.commentId.permlink,
                                    createdPostLiveData.commentId.refBlockNum
                                ),
                                DiscussionIdModel(
                                    createdPostLiveData.parentId.userId, createdPostLiveData.parentId.permlink,
                                    createdPostLiveData.parentId.refBlockNum
                                )
                            )
                        )
                    }
                }
            }
        }
    }

    fun createPostOrComment(request: DiscussionCreationRequest) {

        val requestEntity = when (request) {
            is PostCreationRequestModel -> PostCreationRequestEntity(request.title, request.body, request.tags)
            is CommentCreationRequestModel -> CommentCreationRequestEntity(
                request.body,
                request.parentId,
                request.tags
            )
        }
        lastPostCreationRequest = requestEntity
        discussionCreationRepository.makeAction(requestEntity)
    }

    private fun DiscussionCreationRequestEntity.toEmptyModel() =
        if (this is PostCreationRequestEntity) PostCreationResultModel.empty
        else CommentCreationResultModel.empty
}