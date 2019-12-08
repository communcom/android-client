package io.golos.cyber_android.ui.screens.ftue_search_community.viewmodel

import androidx.lifecycle.MutableLiveData
import io.golos.cyber_android.ui.common.mvvm.viewModel.ViewModelBase
import io.golos.cyber_android.ui.common.mvvm.view_commands.SetLoadingVisibilityCommand
import io.golos.cyber_android.ui.common.paginator.Paginator
import io.golos.cyber_android.ui.dto.Community
import io.golos.cyber_android.ui.mappers.mapToCommunityList
import io.golos.cyber_android.ui.screens.ftue_search_community.model.FtueItemListModelEventProcessor
import io.golos.cyber_android.ui.screens.ftue_search_community.model.FtueSearchCommunityModel
import io.golos.cyber_android.ui.screens.ftue_search_community.model.item.collection.CommunityCollection
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

    private val _collectionListState: MutableLiveData<List<CommunityCollection>> = MutableLiveData(
        listOf(
            CommunityCollection(),
            CommunityCollection(),
            CommunityCollection()
        )
    )

    val collectionListState = _collectionListState.toLiveData()

    private val _haveNeedCollections: MutableLiveData<Boolean> = MutableLiveData(false)

    val haveNeedCollection = _haveNeedCollections.toLiveData()

    init {
        paginator.sideEffectListener = { sideEffect ->
            when (sideEffect) {
                is Paginator.SideEffect.LoadPage -> loadCommunities(sideEffect.pageCount)
                is Paginator.SideEffect.ErrorEvent -> {
                }
            }
        }
        paginator.render = { state ->
            _communityListState.value = state
        }

        loadInitialCommunities()
    }

    override fun onFollowToCommunity(community: Community) {
        launch {
            try {
                addCommunityToCollection(community)
                model.onFollowToCommunity(community.communityId)
            } catch (e: Exception) {
                Timber.e(e)
            }
        }
    }

    override fun onUnFollowFromCommunity(community: Community) {
        launch {
            try {
                onDeleteCommunityFromCollection(community)
                model.onUnFollowFromCommunity(community.communityId)
            } catch (e: Exception) {
                Timber.e(e)
            }
        }
    }

    override fun onRetryLoadCommunity() {
        loadInitialCommunities()
    }

    override fun onDeleteCommunityFromCollection(community: Community) {
        val items = (_collectionListState.value as MutableList<CommunityCollection>)
        val collection = items.toMutableList()
        val replaceElement = collection.find { communityCollection ->
            communityCollection.community == community
        }
        val replacePosition = collection.indexOf(replaceElement)
        collection.removeAt(replacePosition)
        collection.add(CommunityCollection(null))
        _collectionListState.value = collection
        updateStateOfNextButton()
    }

    fun loadMoreCommunities() {
        paginator.proceed(Paginator.Action.LoadMore)
    }

    private fun addCommunityToCollection(community: Community) {
        val items = (_collectionListState.value as MutableList<CommunityCollection>)
        val collection = items.toMutableList()
        val replaceElement = collection.find { communityCollection ->
            communityCollection.community == null
        }
        val replacePosition = collection.indexOf(replaceElement)
        if (replacePosition != -1) {
            collection.removeAt(replacePosition)
            collection.add(replacePosition, CommunityCollection(community))
        } else {
            collection.add(CommunityCollection(community))
        }
        _collectionListState.value = collection
        updateStateOfNextButton()
    }

    private fun updateStateOfNextButton() {
        val items = _collectionListState.value as MutableList<CommunityCollection>
        val sizeOfHaveCollection = items.filter { it.community != null }.size >= 3
        _haveNeedCollections.value = sizeOfHaveCollection
    }

    private fun loadCommunities(pageCount: Int) {
        loadCommunitiesJob = launch {
            try {
                _command.value = SetLoadingVisibilityCommand(true)
                val communityDomain = model.getCommunities(
                    offset = pageCount * PAGINATION_PAGE_SIZE,
                    pageCount = pageCount
                )
                val community = communityDomain.mapToCommunityList()
                    .sortedByDescending { it.subscribersCount }
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