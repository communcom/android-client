package io.golos.cyber_android.ui.screens.subscriptions

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.golos.cyber_android.ui.common.mvvm.paginator.Paginator
import io.golos.cyber_android.ui.common.mvvm.viewModel.ViewModelBase
import io.golos.cyber_android.ui.common.mvvm.view_commands.NavigateToSearchCommunitiesCommand
import io.golos.domain.DispatchersProvider
import kotlinx.coroutines.launch
import javax.inject.Inject

class SubscriptionsViewModel @Inject constructor(dispatchersProvider: DispatchersProvider,
                                                 model: SubscriptionsModel,
                                                 private val paginatorSubscriptions: Paginator.Store<Community>,
                                                 private val paginatorRecommendedCommunities: Paginator.Store<Community>) :
    ViewModelBase<SubscriptionsModel>(dispatchersProvider, model) {

    val generalLoadingProgressVisibilityLiveData: MutableLiveData<Int> = MutableLiveData(View.VISIBLE)

    val recommendedCommunitiesLoadingProgressVisibilityLiveData: MutableLiveData<Int> = MutableLiveData(View.INVISIBLE)

    val communitiesSubscriptionsLoadingProgressVisibilityLiveData: MutableLiveData<Int> = MutableLiveData(View.INVISIBLE)


    private val _subscriptionsState: MutableLiveData<SubscriptionsState> = MutableLiveData(SubscriptionsState.UNDEFINED)

    private val _subscriptionsListState: MutableLiveData<Paginator.State> = MutableLiveData(Paginator.State.Empty)

    private val _recommendedSubscriptionsListState: MutableLiveData<Paginator.State> = MutableLiveData(Paginator.State.Empty)

    val subscriptionsListState = _subscriptionsListState as LiveData<Paginator.State>

    val recommendedSubscriptionsListState = _recommendedSubscriptionsListState as LiveData<Paginator.State>

    val subscriptionsState: LiveData<SubscriptionsState> = _subscriptionsState

    private var communitySearchQuery: String = ""

    init {
        paginatorSubscriptions.sideEffectListener = {
            when(it){
                is Paginator.SideEffect.LoadPage -> getCommunities(it.sequenceKey)
                is Paginator.SideEffect.ErrorEvent -> {}
            }
        }
        paginatorSubscriptions.render = {
            when(it){
                is Paginator.State.EmptyProgress -> communitiesSubscriptionsLoadingProgressVisibilityLiveData.value = View.VISIBLE
                is Paginator.State.NewPageProgress<*> -> communitiesSubscriptionsLoadingProgressVisibilityLiveData.value = View.VISIBLE
                else -> {
                    communitiesSubscriptionsLoadingProgressVisibilityLiveData.value = View.INVISIBLE
                    _subscriptionsListState.value = it
                }
            }
        }

        paginatorRecommendedCommunities.sideEffectListener = {
            when(it){
                is Paginator.SideEffect.LoadPage -> getRecommendedCommunities(it.sequenceKey)
                is Paginator.SideEffect.ErrorEvent -> {}
            }
        }
        paginatorRecommendedCommunities.render = {
            when(it){
                is Paginator.State.EmptyProgress -> recommendedCommunitiesLoadingProgressVisibilityLiveData.value = View.VISIBLE
                is Paginator.State.NewPageProgress<*> -> recommendedCommunitiesLoadingProgressVisibilityLiveData.value = View.VISIBLE
                else -> {
                    recommendedCommunitiesLoadingProgressVisibilityLiveData.value = View.INVISIBLE
                    _subscriptionsListState.value = it
                }
            }
        }
    }

    private fun getCommunities(sequenceKey: String?){
        launch {
            val communitiesByQuery = model.getCommunitiesByQuery(communitySearchQuery, sequenceKey, PAGE_SIZE_LIMIT)
            paginatorSubscriptions.proceed(Paginator.Action.NewPage(communitiesByQuery.sequenceKey, communitiesByQuery.communities))
        }
    }

    private fun getRecommendedCommunities(sequenceKey: String?){
        launch {
            val recommendedCommunities = model.getRecommendedCommunities(sequenceKey, PAGE_SIZE_LIMIT)
            paginatorRecommendedCommunities.proceed(Paginator.Action.NewPage(recommendedCommunities.sequenceKey, recommendedCommunities.communities))
        }
    }

    fun onFindCommunitiesClicked(){
        command.value = NavigateToSearchCommunitiesCommand()
    }

    fun start() {
        if(_subscriptionsState.value == SubscriptionsState.UNDEFINED){
            launch{
                if(model.isContainSubscriptionsOnCommunities()){
                    _subscriptionsState.value = SubscriptionsState.EXIST
                    paginatorSubscriptions.proceed(Paginator.Action.Refresh)
                } else{
                    _subscriptionsState.value = SubscriptionsState.EMPTY
                    paginatorRecommendedCommunities.proceed(Paginator.Action.Refresh)
                }
            }
        }
    }

    fun onCommunitySearchQueryChanged(query: String) {
        communitySearchQuery = query
    }

    enum class SubscriptionsState{
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
        EXIST

    }

    private companion object{

        private const val PAGE_SIZE_LIMIT = 30
    }
}