package io.golos.domain.interactors.action

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import io.golos.domain.DiscussionsFeedRepository
import io.golos.domain.DispatchersProvider
import io.golos.domain.Repository
import io.golos.domain.entities.AuthState
import io.golos.domain.entities.PostEntity
import io.golos.domain.entities.VoteRequestEntity
import io.golos.domain.interactors.UseCase
import io.golos.domain.map
import io.golos.domain.model.AuthRequest
import io.golos.domain.model.PostFeedUpdateRequest
import io.golos.domain.model.QueryResult
import io.golos.domain.model.VoteRequestModel
import io.golos.domain.rules.EntityToModelMapper
import io.golos.domain.rules.ModelToEntityMapper
import kotlinx.coroutines.*

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-03-20.
 */
class VoteUseCase(
    private val authRepository: Repository<AuthState, AuthRequest>,
    private val voteRepository: Repository<VoteRequestEntity, VoteRequestEntity>,
    private val feedRepository: DiscussionsFeedRepository<PostEntity, PostFeedUpdateRequest>,
    private val dispatchersProvider: DispatchersProvider,
    private val voteEntityToModelMapper: EntityToModelMapper<VoteRequestEntity, VoteRequestModel>,
    private val voteModelToEntityMapper: ModelToEntityMapper<VoteRequestModel, VoteRequestEntity>
) : UseCase<Map<VoteRequestModel, QueryResult<VoteRequestModel>>> {

    private val useCaseScope = CoroutineScope(dispatchersProvider.uiDispatcher + SupervisorJob())

    private val observer = Observer<Any> {}
    private val mediator = MediatorLiveData<Any>()
    private val voteReadiness = MutableLiveData<Boolean>()
    private val votingStatesLiveData = MutableLiveData<Map<VoteRequestModel, QueryResult<VoteRequestModel>>>()

    val getVotingRediness: LiveData<Boolean> = voteReadiness

    override fun subscribe() {
        super.subscribe()
        val authState = authRepository.getAsLiveData(authRepository.allDataRequest)

        mediator.addSource(authState) {
            voteReadiness.value = it?.isUserLoggedIn == true
        }
        authState.observeForever(observer)

        voteRepository.getAsLiveData(voteRepository.allDataRequest).observeForever(observer)
        mediator.addSource(voteRepository.getAsLiveData(voteRepository.allDataRequest)) {
            when (it) {
                is VoteRequestEntity.VoteForAPostRequestEntity -> {
                    useCaseScope.launch {
                        delay(2_000)
                        feedRepository.requestDiscussionUpdate(it.discussionIdEntity)
                    }
                }
                else -> {
                }
            }
        }

        voteRepository.updateStates.observeForever(observer)
        mediator.addSource(voteRepository.updateStates) { voteStates ->
            if (voteStates == null) return@addSource
            useCaseScope.launch {
                val newVoteStateMap = withContext(dispatchersProvider.workDispatcher) {
                    val currentVotingStates = votingStatesLiveData.value.orEmpty().toMutableMap()

                    voteStates.forEach { mapEntry ->
                        val id = mapEntry.key as VoteRequestEntity.Id
                        val queryResult = mapEntry.value

                        val oldVotingRequestStateKey =
                            currentVotingStates.keys.find {
                                it.discussionIdEntity.refBlockNum == id.discussionIdEntity.refBlockNum
                                        && queryResult.originalQuery.power == it.power
                            }
                        if (oldVotingRequestStateKey == null || !currentVotingStates.contains(oldVotingRequestStateKey)) {
                            val voteModel = voteEntityToModelMapper(queryResult.originalQuery)
                            currentVotingStates += (voteModel to queryResult.map(voteModel))
                        } else {
                            val oldValue = currentVotingStates[oldVotingRequestStateKey]!!

                            if (oldValue::class.java != queryResult::class.java) {
                                val voteModel = voteEntityToModelMapper(queryResult.originalQuery)
                                currentVotingStates += (voteModel to queryResult.map(voteModel))
                            }
                        }
                    }
                    currentVotingStates
                }

                votingStatesLiveData.value = newVoteStateMap
            }
        }
    }

    override fun unsubscribe() {
        super.unsubscribe()
        val authState = authRepository.getAsLiveData(authRepository.allDataRequest)
        mediator.removeSource(authState)
        authState.removeObserver(observer)
        voteRepository.updateStates.removeObserver(observer)
        mediator.removeSource(voteRepository.updateStates)
        voteRepository.getAsLiveData(voteRepository.allDataRequest).removeObserver(observer)
        mediator.removeSource(voteRepository.getAsLiveData(voteRepository.allDataRequest))
    }


    fun vote(request: VoteRequestModel) {
        if (voteReadiness.value != true) {
            System.err.println("cannot vote yet, waiting for auth to complete")
            votingStatesLiveData.value = votingStatesLiveData.value.orEmpty() + (request to QueryResult.Error(
                IllegalStateException("user not authed yet"),
                request
            ))
        } else {
            useCaseScope.launch(dispatchersProvider.workDispatcher) {
                voteRepository.makeAction(
                    voteModelToEntityMapper(
                        request
                    )
                )
            }
        }
    }


    override val getAsLiveData: LiveData<Map<VoteRequestModel, QueryResult<VoteRequestModel>>>
        get() = votingStatesLiveData
}