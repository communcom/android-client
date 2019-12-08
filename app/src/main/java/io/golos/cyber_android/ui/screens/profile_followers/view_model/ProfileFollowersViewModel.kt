package io.golos.cyber_android.ui.screens.profile_followers.view_model

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.common.mvvm.viewModel.ViewModelBase
import io.golos.cyber_android.ui.common.mvvm.view_commands.ShowMessageCommand
import io.golos.cyber_android.ui.common.recycler_view.versioned.VersionedListItem
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
    private val startFilter: FollowersFilter,
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

    private val _noDataStubVisibility = MutableLiveData<Int>(false.toVisibility())
    val noDataStubVisibility: LiveData<Int> get() = _noDataStubVisibility

    val pageSize = model.pageSize

    init {
        followersItems.observeForever {
            val hasData = it.isNotEmpty() && filter.value == FollowersFilter.FOLLOWERS
            _followersVisibility.value = hasData.toVisibility()
            _noDataStubVisibility.value = (!hasData).toVisibility()
        }

        followingsItems.observeForever {
            val hasData = it.isNotEmpty() && filter.value == FollowersFilter.FOLLOWINGS
            _followingsVisibility.value = hasData.toVisibility()
            _noDataStubVisibility.value = (!hasData).toVisibility()
        }

        mutualsItems.observeForever {
            val hasData = it.isNotEmpty() && filter.value == FollowersFilter.MUTUALS
            _mutualsVisibility.value = hasData.toVisibility()
            _noDataStubVisibility.value = (!hasData).toVisibility()
        }
    }

    fun onViewCreated() = loadPage(startFilter)

    override fun onNextPageReached(filter: FollowersFilter) = loadPage(filter)

    override fun retry(filter: FollowersFilter) {
        launch {
            model.retry(filter)
        }
    }

    override fun onJoinClick(userId: UserIdDomain, filter: FollowersFilter) {
        launch {
            if(!model.subscribeUnsubscribe(userId, filter)) {
                _command.value = ShowMessageCommand(R.string.common_general_error)
            }
        }
    }

    private fun loadPage(filter: FollowersFilter) {
        launch {
            model.loadPage(filter)
        }
    }

    private fun Boolean.toVisibility() = if(this) View.VISIBLE else View.INVISIBLE
}
