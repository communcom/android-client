package io.golos.cyber_android.ui.screens.community_page.view_model

import androidx.lifecycle.MutableLiveData
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.shared.mvvm.viewModel.ViewModelBase
import io.golos.cyber_android.ui.shared.mvvm.view_commands.NavigateBackwardCommand
import io.golos.cyber_android.ui.shared.mvvm.view_commands.SetLoadingVisibilityCommand
import io.golos.cyber_android.ui.shared.mvvm.view_commands.ShowMessageResCommand
import io.golos.cyber_android.ui.screens.community_page.dto.CommunityPage
import io.golos.cyber_android.ui.screens.community_page.dto.NavigateToFriendsCommand
import io.golos.cyber_android.ui.screens.community_page.dto.NavigateToMembersCommand
import io.golos.cyber_android.ui.screens.community_page.dto.SwitchToLeadsTabCommand
import io.golos.cyber_android.ui.screens.community_page.mappers.CommunityPageDomainToCommunityPageMapper
import io.golos.cyber_android.ui.screens.community_page.model.CommunityPageModel
import io.golos.cyber_android.ui.shared.utils.toLiveData
import io.golos.domain.DispatchersProvider
import io.golos.domain.dto.CommunityIdDomain
import io.golos.utils.helpers.EMPTY
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class CommunityPageViewModel @Inject constructor(
    dispatchersProvider: DispatchersProvider,
    model: CommunityPageModel,
    private val communityId: CommunityIdDomain
) : ViewModelBase<CommunityPageModel>(dispatchersProvider, model) {

    private val communityPageMutableLiveData = MutableLiveData<CommunityPage>()

    private val communityPageIsLoadProgressMutableLiveData = MutableLiveData(false)

    private val communityPageIsErrorMutableLiveData = MutableLiveData(false)

    val communityPageLiveData = communityPageMutableLiveData.toLiveData()

    val communityPageIsErrorLiveData = communityPageIsErrorMutableLiveData.toLiveData()

    val communityPageIsLoadProgressLiveData = communityPageIsLoadProgressMutableLiveData.toLiveData()

    private val _swipeRefreshing = MutableLiveData<Boolean>(false)
    val swipeRefreshing get() = _swipeRefreshing.toLiveData()

    fun start() {
        loadCommunityPage()
    }

    fun loadCommunityPage(){
        launch {
            try{
                communityPageIsErrorMutableLiveData.value = false
                communityPageIsLoadProgressMutableLiveData.value = true

                val communityPageDomain = model.getCommunityPageById(communityId)

                val communityPage = CommunityPageDomainToCommunityPageMapper().invoke(communityPageDomain)
                communityPageMutableLiveData.value = communityPage
                communityPageIsErrorMutableLiveData.value = false

            } catch (e: Exception){
                Timber.e(e)
                communityPageIsErrorMutableLiveData.value = true
            } finally {
                communityPageIsLoadProgressMutableLiveData.value = false
            }

        }
    }

    fun onBackPressed() {
        _command.value = NavigateBackwardCommand()
    }

    fun onLeadsLabelClick() {
        _command.value = SwitchToLeadsTabCommand()
    }

    fun onMembersLabelClick() {
        _command.value = NavigateToMembersCommand(communityId)
    }

    fun onFriendsLabelClick() {
        _command.value = NavigateToFriendsCommand(communityPageMutableLiveData.value!!.friends)
    }

    fun changeJoinStatus() {
        launch {
            try {
                _command.value = SetLoadingVisibilityCommand(true)
                val communityPage = communityPageMutableLiveData.value
                val isSubscribed = communityPage?.isSubscribed ?: false
                if(isSubscribed){
                    model.unsubscribeToCommunity(communityId)
                } else{
                    model.subscribeToCommunity(communityId)
                }
                communityPage?.isSubscribed = !isSubscribed
                communityPageMutableLiveData.value = communityPage
                communityPageIsErrorMutableLiveData.value = false
            } catch (e: Exception){
                _command.value = ShowMessageResCommand(R.string.loading_error)
                communityPageIsErrorMutableLiveData.value = true
            } finally {
                _command.value = SetLoadingVisibilityCommand(false)
            }
        }
    }

    fun onSwipeRefresh() {
        launch {
            loadCommunityPage()
            _swipeRefreshing.value = false
        }
    }
}