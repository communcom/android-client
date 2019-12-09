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

    private var communitiesSearchQuery = ""

    val communityListState = _communityListState.toLiveData()

    private val communitiesSubscribstions = mutableListOf<Community>()

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
                _command.value = SetLoadingVisibilityCommand(true)
                model.onFollowToCommunity(community.communityId)
                addCommunityToCollection(community)
            } catch (e: Exception) {
                Timber.e(e)
            } finally {
                _command.value = SetLoadingVisibilityCommand(false)
            }
        }
    }

    override fun onUnFollowFromCommunity(community: Community) {
        launch {
            try {
                _command.value = SetLoadingVisibilityCommand(true)
                model.onUnFollowFromCommunity(community.communityId)
                removeCommunityFromCollection(community)
            } catch (e: Exception) {
                Timber.e(e)
            } finally {
                _command.value = SetLoadingVisibilityCommand(false)
            }
        }
    }

    override fun onRetryLoadCommunity() {
        loadInitialCommunities()
    }

    override fun removeCommunityFromCollection(community: Community) {
        communitiesSubscribstions.remove(community)
        val communitiesCollection: MutableList<CommunityCollection> = arrayListOf()
        for (i in  0 .. 3){
            if(communitiesSubscribstions.size - 1 >= i){
                communitiesCollection.add(CommunityCollection(communitiesSubscribstions[i]))
            } else{
                communitiesCollection.add(CommunityCollection())
            }
        }
        _collectionListState.value = communitiesCollection
        updateStateOfNextButton()
    }

    fun loadMoreCommunities() {
        paginator.proceed(Paginator.Action.LoadMore)
    }

    private fun addCommunityToCollection(community: Community) {
        communitiesSubscribstions.add(community)
        val communitiesCollection: MutableList<CommunityCollection> = arrayListOf()
        for (i in  0 .. 3){
            if(communitiesSubscribstions.size - 1 >= i){
                communitiesCollection.add(CommunityCollection(communitiesSubscribstions[i]))
            } else{
                communitiesCollection.add(CommunityCollection())
            }
        }
        _collectionListState.value = communitiesCollection
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
            }
        }
    }

    override fun onCleared() {
        loadCommunitiesJob?.cancel()
        super.onCleared()
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

    fun onCommunitiesSearchQueryChanged(query: String) {
        if(communitiesSearchQuery != query){
            communitiesSearchQuery = query
            loadCommunitiesJob?.cancel()
            paginator.proceed(Paginator.Action.Search)
        }
    }
}