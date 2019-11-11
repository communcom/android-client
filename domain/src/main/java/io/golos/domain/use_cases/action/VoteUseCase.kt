package io.golos.domain.use_cases.action

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import io.golos.domain.DispatchersProvider
import io.golos.domain.repositories.Repository
import io.golos.domain.dto.VoteRequestEntity
import io.golos.domain.extensions.map
import io.golos.domain.use_cases.UseCase
import io.golos.domain.use_cases.model.DiscussionIdModel
import io.golos.domain.mappers.VoteRequestEntityToModelMapper
import io.golos.domain.mappers.VoteRequestModelToEntityMapper
import io.golos.domain.repositories.AuthStateRepository
import io.golos.domain.requestmodel.QueryResult
import io.golos.domain.requestmodel.VoteRequestModel
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
    private val authRepository: AuthStateRepository,
    private val voteRepository: Repository<VoteRequestEntity, VoteRequestEntity>,
    private val dispatchersProvider: DispatchersProvider,
    private val voteEntityToModelMapper: VoteRequestEntityToModelMapper,
    private val voteModelToEntityMapper: VoteRequestModelToEntityMapper
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
                val newVoteStateMap = withContext(dispatchersProvider.calculationsDispatcher) {
                    voteStates.values
                        .map { it.map(voteEntityToModelMapper.map(it.originalQuery)) }
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
            useCaseScope.launch(dispatchersProvider.calculationsDispatcher) {
                voteRepository.makeAction(
                    voteModelToEntityMapper.map(request)
                )
            }
        }
    }


    override val getAsLiveData: LiveData<MutableMap<DiscussionIdModel, QueryResult<VoteRequestModel>>>
        get() = votingStatesLiveData
}