package io.golos.cyber_android.ui.screens.followers

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.golos.cyber_android.ui.common.mvvm.viewModel.ViewModelBase
import io.golos.cyber_android.ui.common.paginator.Paginator
import io.golos.cyber_android.ui.screens.followers.mappers.FollowersDomainListToFollowersListMapper
import io.golos.cyber_android.ui.screens.subscriptions.Community
import io.golos.domain.DispatchersProvider
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class FollowersViewModel @Inject constructor(
    dispatchersProvider: DispatchersProvider,
    model: FollowersModel,
    private val paginator: Paginator.Store<Community>
) : ViewModelBase<FollowersModel>(dispatchersProvider, model) {

    private var query: String? = null

    private val _followersListStateLiveData: MutableLiveData<Paginator.State> = MutableLiveData(Paginator.State.Empty)

    private val _searchErrorVisibilityLiveData: MutableLiveData<Boolean> = MutableLiveData(false)

    val followersListStateLiveData = _followersListStateLiveData as LiveData<Paginator.State>

    val searchErrorVisibilityLiveData = _searchErrorVisibilityLiveData as LiveData<Boolean>

    init {
        paginator.sideEffectListener = {
            when (it) {
                is Paginator.SideEffect.LoadPage -> loadFollowers(it.sequenceKey)
                is Paginator.SideEffect.ErrorEvent -> {
                    _searchErrorVisibilityLiveData.value = false
                }
            }
        }
        paginator.render = {
            _followersListStateLiveData.value = it
        }
    }

    private fun loadFollowers(sequenceKey: String?) {
        launch {
            try {
                val followersPage = model.getFollowers(query, sequenceKey, PAGE_SIZE_LIMIT)
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
        if (followersListState is Paginator.State.Empty) {
            loadFollowers(null)
        }
    }

    fun back() {

    }

    private companion object {

        private const val PAGE_SIZE_LIMIT = 30
    }
}