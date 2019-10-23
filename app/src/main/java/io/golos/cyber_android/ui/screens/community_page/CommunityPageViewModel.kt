package io.golos.cyber_android.ui.screens.community_page

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.golos.cyber_android.ui.common.mvvm.viewModel.ViewModelBase
import io.golos.cyber_android.ui.screens.community_page.mappers.CommunityPageDomainToCommunityPageMapper
import io.golos.cyber_android.utils.EMPTY
import io.golos.domain.DispatchersProvider
import kotlinx.coroutines.launch
import timber.log.Timber
import java.lang.Exception
import javax.inject.Inject

class CommunityPageViewModel @Inject constructor(
    dispatchersProvider: DispatchersProvider,
    model: CommunityPageModel
) : ViewModelBase<CommunityPageModel>(dispatchersProvider, model) {

    private val communityPageMutableLiveData = MutableLiveData<CommunityPage>()

    private val communityPageIsLoadProgressMutableLiveData = MutableLiveData<Boolean>()

    private val communityPageIsErrorMutableLiveData = MutableLiveData<Boolean>()

    val communityPageLiveData = communityPageMutableLiveData as LiveData<CommunityPage>

    val communityPageIsErrorLiveData = communityPageIsErrorMutableLiveData as LiveData<Boolean>

    val communityPageIsLoadProgressLiveData = communityPageIsLoadProgressMutableLiveData as LiveData<Boolean>

    private var communityId: String = EMPTY

    fun start(communityId: String) {
        this.communityId = communityId
        loadCommunityPage()
    }

    fun loadCommunityPage(){
        launch {
            try{
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
}