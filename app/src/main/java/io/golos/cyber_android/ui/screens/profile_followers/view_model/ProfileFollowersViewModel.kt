package io.golos.cyber_android.ui.screens.profile_followers.view_model

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.shared.mvvm.viewModel.ViewModelBase
import io.golos.cyber_android.ui.shared.mvvm.view_commands.NavigateBackwardCommand
import io.golos.cyber_android.ui.shared.mvvm.view_commands.ShowMessageResCommand
import io.golos.cyber_android.ui.shared.recycler_view.versioned.VersionedListItem
import io.golos.cyber_android.ui.dto.FollowersFilter
import io.golos.cyber_android.ui.screens.profile_followers.model.ProfileFollowersModel
import io.golos.cyber_android.ui.screens.profile_followers.view.list.FollowersListItemEventsProcessor
import io.golos.domain.DispatchersProvider
import io.golos.domain.dto.UserIdDomain
import io.golos.domain.repositories.CurrentUserRepositoryRead
import kotlinx.coroutines.launch
import javax.inject.Inject

class ProfileFollowersViewModel
@Inject
constructor(
    startFilter: FollowersFilter,
    dispatchersProvider: DispatchersProvider,
    model: ProfileFollowersModel,
    currentUserRepository: CurrentUserRepositoryRead
) : ViewModelBase<ProfileFollowersModel>(dispatchersProvider, model),
    FollowersListItemEventsProcessor {

    private val _title = MutableLiveData<String>(currentUserRepository.userName)
    val title: LiveData<String> get() = _title

    val filter = MutableLiveData<FollowersFilter>(startFilter)

    val followersItems: LiveData<List<VersionedListItem>> get() = model.getItems(FollowersFilter.FOLLOWERS)

    val followingsItems: LiveData<List<VersionedListItem>> get() = model.getItems(FollowersFilter.FOLLOWINGS)

    val mutualsItems: LiveData<List<VersionedListItem>> get() = model.getItems(FollowersFilter.MUTUALS)

    private val _followersVisibility = MutableLiveData<Int>((startFilter == FollowersFilter.FOLLOWERS).toVisibility())
    val followersVisibility: LiveData<Int> get() = _followersVisibility

    private val _followingsVisibility = MutableLiveData<Int>((startFilter == FollowersFilter.FOLLOWINGS).toVisibility())
    val followingsVisibility: LiveData<Int> get() = _followingsVisibility

    private val _mutualsVisibility = MutableLiveData<Int>((startFilter == FollowersFilter.MUTUALS).toVisibility())
    val mutualsVisibility: LiveData<Int> get() = _mutualsVisibility

    val pageSize = model.pageSize

    init {
        filter.observeForever {
            switchTab(filter.value!!)
            loadPage(it)
        }
    }

    private fun switchTab(filter: FollowersFilter) {
        when(filter) {
            FollowersFilter.FOLLOWERS -> {
                _followersVisibility.value = View.VISIBLE
                _followingsVisibility.value = View.INVISIBLE
                _mutualsVisibility.value = View.INVISIBLE
            }
            FollowersFilter.FOLLOWINGS -> {
                _followersVisibility.value = View.INVISIBLE
                _followingsVisibility.value = View.VISIBLE
                _mutualsVisibility.value = View.INVISIBLE
            }
            FollowersFilter.MUTUALS -> {
                _followersVisibility.value = View.INVISIBLE
                _followingsVisibility.value = View.INVISIBLE
                _mutualsVisibility.value = View.VISIBLE
            }
        }
    }

    override fun onNextPageReached(filter: FollowersFilter) = loadPage(filter)

    override fun retry(filter: FollowersFilter) {
        launch {
            model.retry(filter)
        }
    }

    override fun onFollowClick(userId: UserIdDomain, filter: FollowersFilter) {
        launch {
            if(!model.subscribeUnsubscribe(userId, filter)) {
                _command.value = ShowMessageResCommand(R.string.common_general_error)
            }
        }
    }

    fun onBackClick() {
        _command.value = NavigateBackwardCommand()
    }

    private fun loadPage(filter: FollowersFilter) {
        launch {
            model.loadPage(filter)
        }
    }

    private fun Boolean.toVisibility() = if(this) View.VISIBLE else View.INVISIBLE
}
