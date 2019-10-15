package io.golos.cyber_android.ui.screens.followers

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.golos.cyber_android.ui.common.mvvm.viewModel.ViewModelBase
import io.golos.cyber_android.ui.common.paginator.Paginator
import io.golos.cyber_android.ui.screens.followers.mappers.FollowersDomainListToFollowersListMapper
import io.golos.cyber_android.ui.screens.subscriptions.Community
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

    val followersListStateLiveData = _followersListStateLiveData as LiveData<Paginator.State>

    val searchErrorVisibilityLiveData = _searchErrorVisibilityLiveData as LiveData<Boolean>

    init {
        paginator.sideEffectListener = {
            when (it) {
                is Paginator.SideEffect.LoadPage -> loadMoreFollowers(it.sequenceKey)
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

    private fun loadMoreFollowers(sequenceKey: String?) {
        followersLoadJob = launch {
            try {
                val followersPage = model.getFollowers(followersSearchQuery, sequenceKey, PAGE_SIZE_LIMIT)
                paginator.proceed(
                    Paginator.Action.NewPage(
                        followersPage.sequenceKey,
                        FollowersDomainListToFollowersListMapper().invoke(followersPage.followers)
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

    }

    fun changeFollowingStatus(follower: Follower) {

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