package io.golos.cyber_android.ui.screens.community_page.view_model

import android.content.Context
import androidx.lifecycle.MutableLiveData
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.screens.community_page.dto.*
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
import io.golos.cyber_android.ui.shared.extensions.getMessage
import io.golos.cyber_android.ui.shared.mvvm.view_commands.ShowMessageTextCommand
import io.golos.cyber_android.ui.shared.utils.toLiveData
import io.golos.domain.DispatchersProvider
import io.golos.domain.dto.CommunityIdDomain
import io.golos.domain.repositories.CurrentUserRepositoryRead
import io.golos.utils.helpers.EMPTY
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import io.golos.data.BuildConfig
class CommunityPageViewModel
@Inject
constructor(
    private val appContext: Context,
    dispatchersProvider: DispatchersProvider,
    model: CommunityPageModel,
    private val currentUserRepositoryRead: CurrentUserRepositoryRead,
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

    fun onCommunitySettingsClick(){
        _command.value = ShowCommunitySettings(communityPageMutableLiveData.value,currentUserRepositoryRead.userId.userId)
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
                _command.value = ShowMessageTextCommand(e.getMessage(appContext))
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

    fun getShareString(communityPage: CommunityPage,currentUserId: String): String {
        return "${BuildConfig.BASE_URL}/${communityPage.communityId.code}?invite=${currentUserId}"
    }
}