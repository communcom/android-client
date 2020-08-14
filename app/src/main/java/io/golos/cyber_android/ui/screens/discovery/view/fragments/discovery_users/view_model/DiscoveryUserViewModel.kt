package io.golos.cyber_android.ui.screens.discovery.view.fragments.discovery_users.view_model

import androidx.lifecycle.LiveData
import io.golos.cyber_android.ui.dto.FollowersFilter
import io.golos.cyber_android.ui.screens.discovery.view.fragments.discovery_users.model.UserDiscoveryModel
import io.golos.cyber_android.ui.screens.profile_followers.view.list.FollowersListItemEventsProcessor
import io.golos.cyber_android.ui.shared.mvvm.viewModel.ViewModelBase
import io.golos.cyber_android.ui.shared.mvvm.view_commands.NavigateToUserProfileCommand
import io.golos.cyber_android.ui.shared.mvvm.view_commands.ShowMessageTextCommand
import io.golos.cyber_android.ui.shared.recycler_view.versioned.VersionedListItem
import io.golos.domain.DispatchersProvider
import io.golos.domain.GlobalConstants
import io.golos.domain.dto.UserIdDomain
import kotlinx.coroutines.launch
import javax.inject.Inject

class DiscoveryUserViewModel
@Inject
constructor(
    private val isLimited:Boolean,
    dispatchersProvider: DispatchersProvider,
    model: UserDiscoveryModel
) : ViewModelBase<UserDiscoveryModel>(
    dispatchersProvider,
    model
), FollowersListItemEventsProcessor {
    var isInitialIsLoaded:Boolean = false
    val followingsItems: LiveData<List<VersionedListItem>> get() = model.getItems(FollowersFilter.FOLLOWINGS)

    val pageSize = if(isLimited) 5 else GlobalConstants.PAGE_SIZE

    init {
        loadPage()
        isInitialIsLoaded = true
    }

    override fun onNextPageReached(filter: FollowersFilter) {
        if(isLimited){
            if(isInitialIsLoaded){

            }else{
                loadPage()
            }
        }else
            loadPage()
    }

    override fun retry(filter: FollowersFilter) {
        launch {
            model.retry(filter)
        }
    }

    override fun onFollowClick(userId: UserIdDomain, filter: FollowersFilter) {
        launch {
            model.subscribeUnsubscribe(userId, filter)?.let {
                _command.value = ShowMessageTextCommand(it.message)
            }
        }
    }

    private fun loadPage() {
        launch {
            model.loadPage(FollowersFilter.FOLLOWINGS)
        }
    }

    override fun onUserClick(userId: UserIdDomain) {
        _command.value = NavigateToUserProfileCommand(userId)
    }


}