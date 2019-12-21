package io.golos.cyber_android.ui.screens.followers

import android.util.Log
import androidx.lifecycle.MutableLiveData
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.common.mvvm.viewModel.ViewModelBase
import io.golos.cyber_android.ui.common.mvvm.view_commands.NavigateBackwardCommand
import io.golos.cyber_android.ui.common.mvvm.view_commands.SetLoadingVisibilityCommand
import io.golos.cyber_android.ui.common.mvvm.view_commands.ShowMessageResCommand
import io.golos.cyber_android.ui.common.paginator.Paginator
import io.golos.cyber_android.ui.screens.followers.mappers.FollowersDomainListToFollowersListMapper
import io.golos.cyber_android.ui.screens.subscriptions.Community
import io.golos.cyber_android.ui.utils.toLiveData
import io.golos.domain.DispatchersProvider
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class FollowersViewModel @Inject constructor(
    dispatchersProvider: DispatchersProvider,
    model: FollowersModel,
    private val paginator: Paginator.Store<Community>
) : ViewModelBase<FollowersModel>(dispatchersProvider, model) {

    private var followersSearchQuery: String? = null

    private val _followersListStateLiveData: MutableLiveData<Paginator.State> = MutableLiveData(Paginator.State.Empty)

    private val _searchErrorVisibilityLiveData: MutableLiveData<Boolean> = MutableLiveData(false)

    private var followersLoadJob: Job? = null

    private val _followingStatusLiveData: MutableLiveData<Follower> = MutableLiveData()

    val followersListStateLiveData = _followersListStateLiveData.toLiveData()

    val followingStatusLiveData = _followingStatusLiveData.toLiveData()

    init {
        paginator.sideEffectListener = {
            when (it) {
                is Paginator.SideEffect.LoadPage -> loadMoreFollowers(it.pageCount)
                is Paginator.SideEffect.ErrorEvent -> {
                    _searchErrorVisibilityLiveData.value = false
                }
            }
        }
        paginator.render = {
            _followersListStateLiveData.value = it
        }
    }

    fun loadMoreFollowers(){
        paginator.proceed(Paginator.Action.LoadMore)
    }

    override fun onCleared() {
        followersLoadJob?.cancel()
        super.onCleared()
    }

    private fun loadMoreFollowers(pageCount: Int) {
        Log.d("PAGING", pageCount.toString())
        followersLoadJob = launch {
            try {
                val offset = pageCount * PAGE_SIZE_LIMIT
                val followers = model.getFollowers(followersSearchQuery, offset, PAGE_SIZE_LIMIT)
                paginator.proceed(
                    Paginator.Action.NewPage(
                        pageCount,
                        FollowersDomainListToFollowersListMapper().invoke(followers)
                    )
                )
            } catch (e: Exception) {
                Timber.e(e)
                paginator.proceed(Paginator.Action.PageError(e))
            }
        }
    }

    fun start() {
        val followersListState = _followersListStateLiveData.value
        if (followersListState is Paginator.State.Empty || followersListState is Paginator.State.EmptyError) {
            paginator.proceed(Paginator.Action.Restart)
        }
    }

    fun back() {
        _command.value = NavigateBackwardCommand()
    }

    fun changeFollowingStatus(follower: Follower) {
        launch {
            try {
                _command.value = SetLoadingVisibilityCommand(true)
                val followerId = follower.userId
                if (follower.isFollowing) {
                    model.unsubscribeToFollower(followerId)
                } else {
                    model.subscribeToFollower(followerId)
                }
                val state: Paginator.State = _followersListStateLiveData.value!!
                follower.isFollowing = !follower.isFollowing
                val updatedState = updateFollowerSubscriptionStatusInState(state, follower)
                _followersListStateLiveData.value = updatedState
                _followingStatusLiveData.value = follower
                _command.value = SetLoadingVisibilityCommand(false)
            } catch (e: Exception) {
                Timber.e(e)
                _command.value = ShowMessageResCommand(R.string.loading_error)
                _command.value = SetLoadingVisibilityCommand(false)
            }
        }
    }

    private fun updateFollowerSubscriptionStatusInState(state: Paginator.State, follower: Follower): Paginator.State {
        when (state) {
            is Paginator.State.Data<*> -> {
                val findFollower = (state.data as List<Follower>).find { it == follower }
                findFollower?.isFollowing = follower.isFollowing
            }
            is Paginator.State.Refresh<*> -> {
                val findCommunity = (state.data as List<Follower>).find { it == follower }
                findCommunity?.isFollowing = follower.isFollowing
            }
            is Paginator.State.NewPageProgress<*> -> {
                val findCommunity = (state.data as List<Follower>).find { it == follower }
                findCommunity?.isFollowing = follower.isFollowing
            }
            is Paginator.State.FullData<*> -> {
                val findCommunity = (state.data as List<Follower>).find { it == follower }
                findCommunity?.isFollowing = follower.isFollowing
            }
        }
        return state
    }

    fun onFollowersSearchQueryChanged(query: String) {
        if(followersSearchQuery == query){
            _searchErrorVisibilityLiveData.value = false
        } else{
            followersSearchQuery = query
            followersLoadJob?.cancel()
            paginator.proceed(Paginator.Action.Search)
        }
    }

    private companion object {

        private const val PAGE_SIZE_LIMIT = 30
    }
}