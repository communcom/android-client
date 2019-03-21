package io.golos.domain.interactors.action

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import io.golos.domain.DiscussionsFeedRepository
import io.golos.domain.Repository
import io.golos.domain.entities.AuthState
import io.golos.domain.entities.CyberUser
import io.golos.domain.entities.PostEntity
import io.golos.domain.entities.VoteRequestEntity
import io.golos.domain.interactors.UseCase
import io.golos.domain.model.AuthRequest
import io.golos.domain.model.PostFeedUpdateRequest
import io.golos.domain.model.QueryResult
import io.golos.domain.model.VoteRequest

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-03-20.
 */
class VoteUseCase(
    private val authRepository: Repository<AuthState, AuthRequest>,
    private val voteRepository: Repository<VoteRequestEntity, VoteRequestEntity>,
    private val feedRepository: DiscussionsFeedRepository<PostEntity, PostFeedUpdateRequest>
) : UseCase<Map<VoteRequest, QueryResult<VoteRequest>>> {

    private val observer = Observer<Any> {}
    private val mediator = MediatorLiveData<Any>()
    private val voteReadiness = MutableLiveData<Boolean>()
    private val authStates = MutableLiveData<Map<VoteRequest, QueryResult<VoteRequest>>>()

    val getVotingRediness: LiveData<Boolean> = voteReadiness


    override fun subscribe() {
        super.subscribe()
        val emptyRequest = AuthRequest(CyberUser(""), "")
        val authState = authRepository.getAsLiveData(emptyRequest)
        mediator.addSource(authState) {
            voteReadiness.value = it?.isUserLoggedIn == true
        }
        authState.observeForever(observer)
    }

    override fun unsubscribe() {
        super.unsubscribe()
        val emptyRequest = AuthRequest(CyberUser(""), "")
        val authState = authRepository.getAsLiveData(emptyRequest)
        mediator.removeSource(authState)
        authState.removeObserver(observer)
    }


    fun vote(request: VoteRequest) {
        if (voteReadiness.value != true) {
            System.err.println("cannot vote yet, waiting for auth to complete")
        } else {

        }
    }


    override val getAsLiveData: LiveData<Map<VoteRequest, QueryResult<VoteRequest>>>
        get() = authStates
}