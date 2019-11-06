package io.golos.cyber_android.ui.screens.community_page

import androidx.lifecycle.MutableLiveData
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.common.mvvm.viewModel.ViewModelBase
import io.golos.cyber_android.ui.common.mvvm.view_commands.BackCommand
import io.golos.cyber_android.ui.common.mvvm.view_commands.SetLoadingVisibilityCommand
import io.golos.cyber_android.ui.common.mvvm.view_commands.ShowMessageCommand
import io.golos.cyber_android.ui.screens.community_page.mappers.CommunityPageDomainToCommunityPageMapper
import io.golos.cyber_android.utils.EMPTY
import io.golos.cyber_android.utils.toLiveData
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

    private var communityId: String = EMPTY

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
        command.value = BackCommand()
    }

    fun onNotificationCommunityControlClicked() {

    }

    fun changeJoinStatus() {
        launch {
            try {
                command.value = SetLoadingVisibilityCommand(true)
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
                command.value = ShowMessageCommand(R.string.loading_error)
                communityPageIsErrorMutableLiveData.value = true
            } finally {
                command.value = SetLoadingVisibilityCommand(false)
            }
        }
    }
}