package io.golos.cyber_android.ui.screens.ftue_search_community.viewmodel

import androidx.lifecycle.MutableLiveData
import io.golos.cyber_android.ui.common.mvvm.viewModel.ViewModelBase
import io.golos.cyber_android.ui.common.mvvm.view_commands.SetLoadingVisibilityCommand
import io.golos.cyber_android.ui.common.paginator.Paginator
import io.golos.cyber_android.ui.dto.Community
import io.golos.cyber_android.ui.mappers.mapToCommunityList
import io.golos.cyber_android.ui.screens.ftue_search_community.model.FtueItemListModelEventProcessor
import io.golos.cyber_android.ui.screens.ftue_search_community.model.FtueSearchCommunityModel
import io.golos.cyber_android.ui.utils.PAGINATION_PAGE_SIZE
import io.golos.cyber_android.ui.utils.toLiveData
import io.golos.domain.DispatchersProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class FtueSearchCommunityViewModel @Inject constructor(
    dispatchersProvider: DispatchersProvider,
    model: FtueSearchCommunityModel,
    private val paginator: Paginator.Store<Community>
) : ViewModelBase<FtueSearchCommunityModel>(dispatchersProvider, model), FtueItemListModelEventProcessor {

    private var loadCommunitiesJob: Job? = null

    private val _communityListState: MutableLiveData<Paginator.State> = MutableLiveData(Paginator.State.Empty)

    val communityListState = _communityListState.toLiveData()

    init {
        paginator.sideEffectListener = { sideEffect ->
            when (sideEffect) {
                is Paginator.SideEffect.LoadPage -> loadCommunities(sideEffect.pageCount)
                is Paginator.SideEffect.ErrorEvent -> {}
            }
        }
        paginator.render = { state ->
            _communityListState.value = state
        }

        loadInitialCommunities()
    }

    override fun onFollowToCommunity(communityId: String) {
        launch {
            try {
                model.onFollowToCommunity(communityId)
                //todo add to finish adapter
            } catch (e: Exception) {
                Timber.e(e)
            }
        }
    }

    override fun onUnFollowFromCommunity(communityId: String) {
        launch {
            try {
                model.onUnFollowFromCommunity(communityId)
                //todo remove from finish adapter
            } catch (e: Exception) {
                Timber.e(e)
            }
        }
    }

    override fun onRetryLoadCommunity() {
        loadInitialCommunities()
    }

    fun loadMoreCommunities() {
        paginator.proceed(Paginator.Action.LoadMore)
    }

    private fun loadCommunities(pageCount: Int) {
        loadCommunitiesJob = launch {
            try {
                _command.value = SetLoadingVisibilityCommand(true)
                val communityDomain = model.getCommunities(
                    offset = pageCount * PAGINATION_PAGE_SIZE,
                    pageCount = pageCount
                )
                val community = communityDomain.mapToCommunityList().sortedBy { it.subscribersCount }
                launch(Dispatchers.Main) {
                    paginator.proceed(Paginator.Action.NewPage(pageCount, community))
                }
            } catch (e: Exception) {
                Timber.e(e)
                paginator.proceed(Paginator.Action.PageError(e))
            } finally {
                _command.value = SetLoadingVisibilityCommand(false)
            }
        }
    }

    private fun restartLoadCommunities() {
        loadCommunitiesJob?.cancel()
        paginator.proceed(Paginator.Action.Restart)
    }

    private fun loadInitialCommunities() {
        val postsListState = _communityListState.value
        if (postsListState is Paginator.State.Empty || postsListState is Paginator.State.EmptyError) {
            restartLoadCommunities()
        }
    }
}