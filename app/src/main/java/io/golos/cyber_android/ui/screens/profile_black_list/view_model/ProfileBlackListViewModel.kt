package io.golos.cyber_android.ui.screens.profile_black_list.view_model

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.common.mvvm.viewModel.ViewModelBase
import io.golos.cyber_android.ui.common.mvvm.view_commands.NavigateBackwardCommand
import io.golos.cyber_android.ui.common.mvvm.view_commands.ShowMessageResCommand
import io.golos.cyber_android.ui.common.recycler_view.versioned.VersionedListItem
import io.golos.cyber_android.ui.dto.BlackListFilter
import io.golos.cyber_android.ui.screens.profile_black_list.model.ProfileBlackListModel
import io.golos.cyber_android.ui.screens.profile_black_list.view.list.BlackListListItemEventsProcessor
import io.golos.domain.DispatchersProvider
import io.golos.domain.dto.UserIdDomain
import kotlinx.coroutines.launch
import javax.inject.Inject

class ProfileBlackListViewModel
@Inject
constructor(
    startFilter: BlackListFilter,
    dispatchersProvider: DispatchersProvider,
    model: ProfileBlackListModel
) : ViewModelBase<ProfileBlackListModel>(dispatchersProvider, model),
    BlackListListItemEventsProcessor {

    val filter = MutableLiveData<BlackListFilter>(startFilter)

    val usersItems: LiveData<List<VersionedListItem>> get() = model.getItems(BlackListFilter.USERS)

    val communitiesItems: LiveData<List<VersionedListItem>> get() = model.getItems(BlackListFilter.COMMUNITIES)

    private val _usersVisibility = MutableLiveData<Int>((startFilter == BlackListFilter.USERS).toVisibility())
    val usersVisibility: LiveData<Int> get() = _usersVisibility

    private val _communitiesVisibility = MutableLiveData<Int>((startFilter == BlackListFilter.COMMUNITIES).toVisibility())
    val communitiesVisibility: LiveData<Int> get() = _communitiesVisibility

    private val _noDataStubVisibility = MutableLiveData<Int>(false.toVisibility())
    val noDataStubVisibility: LiveData<Int> get() = _noDataStubVisibility

    private val _noDataStubText = MutableLiveData<Int>()
    val noDataStubText: LiveData<Int> get() = _noDataStubText

    private val _noDataExplanationText = MutableLiveData<Int>()
    val noDataExplanationText: LiveData<Int> get() = _noDataExplanationText

    val pageSize = model.pageSize

    private var hasUsersData: Boolean? = null
    private var hasCommunitiesData: Boolean? = null

    init {
        filter.observeForever {
            switchTab(filter.value!!)
            loadPage(it)
        }

        usersItems.observeForever {
            hasUsersData = it.isNotEmpty()
            switchTab(filter.value!!)
        }

        communitiesItems.observeForever {
            hasCommunitiesData = it.isNotEmpty()
            switchTab(filter.value!!)
        }
    }

    private fun switchTab(filter: BlackListFilter) {
        when(filter) {
            BlackListFilter.COMMUNITIES ->
                hasCommunitiesData?.let {
                    if(it) {
                        _communitiesVisibility.value = it.toVisibility()
                        _usersVisibility.value = (!it).toVisibility()
                    }
                    _noDataStubVisibility.value = (!it).toVisibility()
                    _noDataStubText.value = R.string.no_communities
                    _noDataExplanationText.value = R.string.no_communities_explanation
                }
            BlackListFilter.USERS ->
                hasUsersData?.let {
                    if(it) {
                        _usersVisibility.value = it.toVisibility()
                        _communitiesVisibility.value = (!it).toVisibility()
                    }

                    _noDataStubVisibility.value = (!it).toVisibility()
                    _noDataStubText.value = R.string.no_users
                    _noDataExplanationText.value = R.string.no_users_explanation
                }
        }
    }

    override fun onNextPageReached(filter: BlackListFilter) = loadPage(filter)

    override fun retry(filter: BlackListFilter) {
        launch {
            model.retry(filter)
        }
    }

    override fun onHideUserClick(userId: UserIdDomain) {
        launch {
            if(!model.switchUserState(userId)) {
                _command.value = ShowMessageResCommand(R.string.common_general_error)
            }
        }
    }

    override fun onHideCommunityClick(communityId: String) {
        launch {
            if(!model.switchCommunityState(communityId)) {
                _command.value = ShowMessageResCommand(R.string.common_general_error)
            }
        }
    }

    fun onBackClick() {
        _command.value = NavigateBackwardCommand()
    }

    private fun loadPage(filter: BlackListFilter) {
        launch {
            model.loadPage(filter)
        }
    }

    private fun Boolean.toVisibility() = if(this) View.VISIBLE else View.INVISIBLE
}
