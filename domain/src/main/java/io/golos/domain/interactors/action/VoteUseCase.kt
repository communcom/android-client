package io.golos.domain.interactors.action

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import io.golos.domain.DispatchersProvider
import io.golos.domain.Repository
import io.golos.domain.entities.AuthState
import io.golos.domain.entities.VoteRequestEntity
import io.golos.domain.interactors.UseCase
import io.golos.domain.interactors.model.DiscussionIdModel
import io.golos.domain.map
import io.golos.domain.requestmodel.AuthRequest
import io.golos.domain.requestmodel.QueryResult
import io.golos.domain.requestmodel.VoteRequestModel
import io.golos.domain.rules.EntityToModelMapper
import io.golos.domain.rules.ModelToEntityMapper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-03-20.
 */
class VoteUseCase
@Inject
constructor(
    private val authRepository: Repository<AuthState, AuthRequest>,
    private val voteRepository: Repository<VoteRequestEntity, VoteRequestEntity>,
    private val dispatchersProvider: DispatchersProvider,
    private val voteEntityToModelMapper: EntityToModelMapper<VoteRequestEntity, VoteRequestModel>,
    private val voteModelToEntityMapper: ModelToEntityMapper<VoteRequestModel, VoteRequestEntity>
) : UseCase<MutableMap<DiscussionIdModel, QueryResult<VoteRequestModel>>> {

    private val useCaseScope = CoroutineScope(dispatchersProvider.uiDispatcher + SupervisorJob())

    private val observer = Observer<Any> {}
    private val mediator = MediatorLiveData<Any>()
    private val voteReadiness = MutableLiveData<Boolean>()
    private val votingStatesLiveData = MutableLiveData<MutableMap<DiscussionIdModel, QueryResult<VoteRequestModel>>>()

    val getVotingReadiness: LiveData<Boolean> = voteReadiness

    override fun subscribe() {
        super.subscribe()

        mediator.addSource(authRepository.getAsLiveData(authRepository.allDataRequest)) {
            voteReadiness.value = it?.isUserLoggedIn == true
        }
        mediator.addSource(voteRepository.updateStates) { voteStates ->
            if (voteStates == null) return@addSource
            useCaseScope.launch {
                val newVoteStateMap = withContext(dispatchersProvider.workDispatcher) {
                    voteStates.values
                        .map { it.map(voteEntityToModelMapper(it.originalQuery)) }
                        .associateBy { it.originalQuery.discussionIdEntity }
                        .toMutableMap()
                }

                votingStatesLiveData.value = newVoteStateMap
            }
        }

        mediator.observeForever(observer)
    }

    override fun unsubscribe() {
        super.unsubscribe()
        mediator.removeSource(authRepository.getAsLiveData(authRepository.allDataRequest))
        mediator.removeSource(voteRepository.updateStates)
        mediator.removeObserver(observer)
    }


    fun vote(request: VoteRequestModel) {
        if (voteReadiness.value != true) {
            System.err.println("cannot vote yet, waiting for auth to complete")
            val map = (votingStatesLiveData.value.orEmpty() + (request.discussionIdEntity to QueryResult.Error(
                IllegalStateException("user not authed yet"),
                request
            ))).toMutableMap()
            votingStatesLiveData.value = map
        } else {
            useCaseScope.launch(dispatchersProvider.workDispatcher) {
                voteRepository.makeAction(
                    voteModelToEntityMapper(request)
                )
            }
        }
    }


    override val getAsLiveData: LiveData<MutableMap<DiscussionIdModel, QueryResult<VoteRequestModel>>>
        get() = votingStatesLiveData
}