package io.golos.cyber_android.ui.screens.community_page.view_model

import androidx.lifecycle.MutableLiveData
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.common.mvvm.viewModel.ViewModelBase
import io.golos.cyber_android.ui.common.mvvm.view_commands.BackCommand
import io.golos.cyber_android.ui.common.mvvm.view_commands.SetLoadingVisibilityCommand
import io.golos.cyber_android.ui.common.mvvm.view_commands.ShowMessageCommand
import io.golos.cyber_android.ui.screens.community_page.dto.CommunityPage
import io.golos.cyber_android.ui.screens.community_page.mappers.CommunityPageDomainToCommunityPageMapper
import io.golos.cyber_android.ui.screens.community_page.model.CommunityPageModel
import io.golos.utils.EMPTY
import io.golos.cyber_android.ui.utils.toLiveData
import io.golos.domain.DispatchersProvider
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class CommunityPageViewModel @Inject constructor(
    dispatchersProvider: DispatchersProvider,
    model: CommunityPageModel
) : ViewModelBase<CommunityPageModel>(dispatchersProvider, model) {

    private val communityPageMutableLiveData = MutableLiveData<CommunityPage>()

    private val communityPageIsLoadProgressMutableLiveData = MutableLiveData(false)

    private val communityPageIsErrorMutableLiveData = MutableLiveData(false)

    val communityPageLiveData = communityPageMutableLiveData.toLiveData()

    val communityPageIsErrorLiveData = communityPageIsErrorMutableLiveData.toLiveData()

    val communityPageIsLoadProgressLiveData = communityPageIsLoadProgressMutableLiveData.toLiveData()

    private var communityId: String = io.golos.utils.EMPTY

    fun start(communityId: String) {
        this.communityId = communityId
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
        _command.value = BackCommand()
    }

    fun onNotificationCommunityControlClicked() {

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
                _command.value = ShowMessageCommand(R.string.loading_error)
                communityPageIsErrorMutableLiveData.value = true
            } finally {
                _command.value = SetLoadingVisibilityCommand(false)
            }
        }
    }
}