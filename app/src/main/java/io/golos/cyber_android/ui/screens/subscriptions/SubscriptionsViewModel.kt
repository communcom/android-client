package io.golos.cyber_android.ui.screens.subscriptions

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.common.mvvm.paginator.Paginator
import io.golos.cyber_android.ui.common.mvvm.viewModel.ViewModelBase
import io.golos.cyber_android.ui.common.mvvm.view_commands.BackCommand
import io.golos.cyber_android.ui.common.mvvm.view_commands.NavigateToSearchCommunitiesCommand
import io.golos.cyber_android.ui.common.mvvm.view_commands.SetLoadingVisibilityCommand
import io.golos.cyber_android.ui.common.mvvm.view_commands.ShowMessageCommand
import io.golos.cyber_android.ui.screens.subscriptions.mappers.CommunityDomainListToCommunityListMapper
import io.golos.cyber_android.utils.EMPTY
import io.golos.domain.DispatchersProvider
import io.golos.domain.Logger
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

class SubscriptionsViewModel @Inject constructor(
    dispatchersProvider: DispatchersProvider,
    model: SubscriptionsModel,
    private val paginatorSubscriptions: Paginator.Store<Community>,
    private val paginatorRecommendedCommunities: Paginator.Store<Community>,
    private val logger: Logger) :

    ViewModelBase<SubscriptionsModel>(dispatchersProvider, model) {

    private val _generalLoadingProgressVisibilityLiveData: MutableLiveData<Boolean> = MutableLiveData(false)

    private val _generalErrorVisibilityLiveData: MutableLiveData<Boolean> = MutableLiveData(false)

    private val _subscriptionsState: MutableLiveData<SubscriptionsState> = MutableLiveData(SubscriptionsState.UNDEFINED)

    private val _subscriptionsListStateLiveData: MutableLiveData<Paginator.State> = MutableLiveData(Paginator.State.Empty)

    private val _recommendedSubscriptionsListStateLiveData: MutableLiveData<Paginator.State> = MutableLiveData(Paginator.State.Empty)

    private val _recommendedSubscriptionStatusLiveData: MutableLiveData<Community> = MutableLiveData()

    private val _subscriptionStatusLiveData: MutableLiveData<Community> = MutableLiveData()

    val subscriptionsListStateLiveData = _subscriptionsListStateLiveData as LiveData<Paginator.State>

    val recommendedSubscriptionsListStateLiveData = _recommendedSubscriptionsListStateLiveData as LiveData<Paginator.State>

    val subscriptionsStateLiveData: LiveData<SubscriptionsState> = _subscriptionsState

    val generalLoadingProgressVisibilityLiveData = _generalLoadingProgressVisibilityLiveData as LiveData<Boolean>

    val recommendedSubscriptionStatusLiveData = _recommendedSubscriptionStatusLiveData as LiveData<Community>

    val subscriptionsStatusLiveData = _subscriptionStatusLiveData as LiveData<Community>

    val generalErrorVisibilityLiveData = _generalErrorVisibilityLiveData as LiveData<Boolean>

    private val recommendedCommunitiesList = mutableListOf<Community>()

    private val communitiesList = mutableListOf<Community>()

    private var communitySearchQuery: String = EMPTY

    private var getCommunitiesJob: Job? = null

    init {
        paginatorSubscriptions.sideEffectListener = {
            when (it) {
                is Paginator.SideEffect.LoadPage -> getCommunities(it.sequenceKey)
                is Paginator.SideEffect.ErrorEvent -> {
                    command.value = ShowMessageCommand(R.string.loading_error)
                }
            }
        }
        paginatorSubscriptions.render = {
            _subscriptionsListStateLiveData.value = it
        }

        paginatorRecommendedCommunities.sideEffectListener = {
            when (it) {
                is Paginator.SideEffect.LoadPage -> getRecommendedCommunities(it.sequenceKey)
                is Paginator.SideEffect.ErrorEvent -> {
                    command.value = ShowMessageCommand(R.string.loading_error)
                }
            }
        }
        paginatorRecommendedCommunities.render = {
            _recommendedSubscriptionsListStateLiveData.value = it
        }
    }

    private fun getCommunities(sequenceKey: String?) {
        getCommunitiesJob?.cancel()
        getCommunitiesJob = launch {
            try {
                val communitiesByQueryPage = model.getCommunitiesByQuery(communitySearchQuery, sequenceKey, PAGE_SIZE_LIMIT)
                if(sequenceKey == null){
                    communitiesList.clear()
                }
                communitiesList.addAll(CommunityDomainListToCommunityListMapper().invoke(communitiesByQueryPage.communities))
                paginatorSubscriptions.proceed(
                    Paginator.Action.NewPage(
                        communitiesByQueryPage.sequenceKey,
                        communitiesList
                    )
                )
            } catch (e: java.lang.Exception) {
                logger.log(e)
                paginatorSubscriptions.proceed(Paginator.Action.PageError(e))
            }
        }
    }

    private fun getRecommendedCommunities(sequenceKey: String?) {
        launch {
            try {
                val recommendedCommunitiesPage = model.getRecommendedCommunities(sequenceKey, PAGE_SIZE_LIMIT)
                if(sequenceKey == null){
                    recommendedCommunitiesList.clear()
                }
                recommendedCommunitiesList.addAll(CommunityDomainListToCommunityListMapper().invoke(recommendedCommunitiesPage.communities))
                paginatorRecommendedCommunities.proceed(
                    Paginator.Action.NewPage(
                        recommendedCommunitiesPage.sequenceKey,
                        recommendedCommunitiesList
                    )
                )
            } catch (e : java.lang.Exception){
                logger.log(e)
                paginatorRecommendedCommunities.proceed(Paginator.Action.PageError(e))
            }
        }
    }

    fun onFindCommunitiesClicked() {
        command.value = NavigateToSearchCommunitiesCommand()
    }

    fun start() {
        if (_subscriptionsState.value == SubscriptionsState.UNDEFINED) {
            launch {
                try {
                    _generalErrorVisibilityLiveData.value = false
                    _generalLoadingProgressVisibilityLiveData.value = true
                    val recommendedCommunitiesPage = model.getRecommendedCommunities(null, PAGE_SIZE_LIMIT)
                    val communitiesByQueryPage = model.getCommunitiesByQuery(communitySearchQuery, null, PAGE_SIZE_LIMIT)
                    if (communitiesByQueryPage.communities.isEmpty()) {
                        val recommendedCommunities = CommunityDomainListToCommunityListMapper().invoke(recommendedCommunitiesPage.communities)
                        val state = Paginator.State.Data(recommendedCommunitiesPage.sequenceKey, recommendedCommunities)
                        paginatorRecommendedCommunities.initState(state)
                        _subscriptionsState.value = SubscriptionsState.EMPTY
                        _recommendedSubscriptionsListStateLiveData.value = state
                    } else {
                        val recommendedCommunities = CommunityDomainListToCommunityListMapper().invoke(communitiesByQueryPage.communities)
                        val state = Paginator.State.Data(communitiesByQueryPage.sequenceKey, recommendedCommunities)
                        paginatorSubscriptions.initState(state)
                        _subscriptionsState.value = SubscriptionsState.EXIST
                        _subscriptionsListStateLiveData.value = state
                    }
                    _generalLoadingProgressVisibilityLiveData.value = false
                } catch (e: Exception) {
                    logger.log(e)
                    _subscriptionsState.value = SubscriptionsState.ERROR
                    _generalLoadingProgressVisibilityLiveData.value = false
                    _generalErrorVisibilityLiveData.value = true
                }
            }
        }
    }

    fun onCommunitySearchQueryChanged(query: String) {
        if(communitySearchQuery != query){
            communitySearchQuery = query
            getCommunitiesJob?.cancel()
            paginatorSubscriptions.proceed(Paginator.Action.Search)
        }
    }

    fun loadMoreRecommendedCommunities() {
        paginatorRecommendedCommunities.proceed(Paginator.Action.LoadMore)
    }

    fun loadSubscriptions() {
        paginatorSubscriptions.proceed(Paginator.Action.LoadMore)
    }

    fun changeCommunitySubscriptionStatus(community: Community) {
        launch {
            command.value = SetLoadingVisibilityCommand(true)
            val communityId = community.communityId
            if(community.isSubscribed){
                model.unsubscribeToCommunity(communityId)
            } else{
                model.subscribeToCommunity(communityId)
            }
            val isRecommendedState = _subscriptionsState.value == SubscriptionsState.EMPTY
            val state: Paginator.State = if(isRecommendedState){
                _recommendedSubscriptionsListStateLiveData.value!!
            } else{
                _subscriptionsListStateLiveData.value!!
            }
            community.isSubscribed = !community.isSubscribed
            val updatedState = updateCommunitySubscriptionStatusInState(state, community)
            if(isRecommendedState){
                _recommendedSubscriptionsListStateLiveData.value = updatedState
                _recommendedSubscriptionStatusLiveData.value = community
            } else{
                _subscriptionsListStateLiveData.value = updatedState
                _subscriptionStatusLiveData.value = community
            }
            command.value = SetLoadingVisibilityCommand(false)
        }
    }

    private fun updateCommunitySubscriptionStatusInState(state: Paginator.State, community: Community): Paginator.State{
        when(state){
            is Paginator.State.Data<*> -> {
                val findCommunity = (state.data as List<Community>).find { it == community }
                findCommunity?.isSubscribed = community.isSubscribed
            }
            is Paginator.State.Refresh<*> -> {
                val findCommunity = (state.data as List<Community>).find { it == community }
                findCommunity?.isSubscribed = community.isSubscribed
            }
            is Paginator.State.NewPageProgress<*> -> {
                val findCommunity = (state.data as List<Community>).find { it == community }
                findCommunity?.isSubscribed = community.isSubscribed
            }
            is Paginator.State.FullData<*> -> {
                val findCommunity = (state.data as List<Community>).find { it == community }
                findCommunity?.isSubscribed = community.isSubscribed
            }
        }
        return state
    }

    fun back() {
        command.value = BackCommand()
    }

    enum class SubscriptionsState {
        /**
         * Yet not unknown count subscriptions
         */
        UNDEFINED,

        /**
         * Count user subscriptions on community = 0
         */
        EMPTY,

        /**
         * Exist subscriptions in community
         */
        EXIST,

        /**
         * Error get data
         */
        ERROR
    }

    private companion object {

        private const val PAGE_SIZE_LIMIT = 30
    }
}