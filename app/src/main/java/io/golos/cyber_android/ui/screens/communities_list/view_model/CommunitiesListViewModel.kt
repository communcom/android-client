package io.golos.cyber_android.ui.screens.communities_list.view_model

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.golos.cyber_android.ui.screens.communities_list.model.CommunitiesListModel
import io.golos.cyber_android.ui.screens.communities_list.view.list.CommunityListItemEventsProcessor
import io.golos.cyber_android.ui.shared.mvvm.viewModel.ViewModelBase
import io.golos.cyber_android.ui.shared.mvvm.view_commands.NavigateBackwardCommand
import io.golos.cyber_android.ui.shared.mvvm.view_commands.NavigateToCommunityPageCommand
import io.golos.cyber_android.ui.shared.mvvm.view_commands.ShowMessageTextCommand
import io.golos.cyber_android.ui.shared.recycler_view.versioned.VersionedListItem
import io.golos.cyber_android.ui.shared.utils.toLiveData
import io.golos.domain.DispatchersProvider
import io.golos.domain.dependency_injection.Clarification
import io.golos.domain.dto.CommunityDomain
import io.golos.domain.dto.CommunityIdDomain
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named
import io.golos.cyber_android.application.App
import io.golos.cyber_android.ui.screens.communities_list.view.view_commands.ShowUnblockDialogCommand
import io.golos.cyber_android.ui.shared.extensions.getMessage
import java.lang.Exception

class CommunitiesListViewModel
@Inject
constructor(
    @Named(Clarification.BACK_BUTTON)
    isBackButtonVisible: Boolean,
    @Named(Clarification.TOOLBAR)
    isToolbarVisible:Boolean,
    dispatchersProvider: DispatchersProvider,
    model: CommunitiesListModel
) : ViewModelBase<CommunitiesListModel>(dispatchersProvider, model), CommunityListItemEventsProcessor {

    var loadNext: Boolean = true

    private val _backButtonVisibility = MutableLiveData<Int>(if(isBackButtonVisible) View.VISIBLE else View.INVISIBLE)
    val backButtonVisibility: LiveData<Int> get() = _backButtonVisibility

    private val _toolbarVisibility = MutableLiveData<Int>(if(isToolbarVisible) View.VISIBLE else View.GONE)
    val toolbarVisibility:LiveData<Int> get() = _toolbarVisibility

    private val _swipeRefreshing = MutableLiveData<Boolean>(false)
    val swipeRefreshing get() = _swipeRefreshing.toLiveData()

    val items: LiveData<List<VersionedListItem>>
        get()  = model.items

    var pageSize: Int = model.pageSize
        private set

    fun onViewCreated() = loadPage()

    override fun onItemClick(community: CommunityDomain) {
        _command.value = NavigateToCommunityPageCommand(community.communityId)
    }

    override fun onNextPageReached(){
        if(loadNext){
            loadPage()
        }
    }

    override fun retry() {
        launch {
            model.retry()
        }
    }

    override fun onJoinClick(communityId: CommunityIdDomain, isInBlockList:Boolean) {
        launch {
            if(isInBlockList){
                _command.postValue(ShowUnblockDialogCommand(communityId))
            }else{
                model.subscribeUnsubscribe(communityId)?.let {
                    _command.value = ShowMessageTextCommand(it.message)
                }
            }
        }
    }

    fun joinWithUnblock(communityId: CommunityIdDomain){
        launch {
            try {
                model.unblockCommunity(communityId)
                model.subscribeUnsubscribe(communityId)
            }catch (e:Exception){
                _command.postValue(ShowMessageTextCommand(e.getMessage(App.mInstance.applicationContext)))
            }
        }
    }

    fun onBackButtonClick() {
        _command.value = NavigateBackwardCommand()
    }

    fun onSwipeRefresh() {
        launch {
            _swipeRefreshing.value = false

            if(model.clear()) {
                model.loadPage()
            }
        }
    }

    private fun loadPage() {
        launch {
            model.loadPage()
        }
    }

    fun changePageSizeTo(size:Int){
        pageSize = size
        model.pageSize = size
    }

}