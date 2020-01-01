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

    private val _noDataStubVisibility = MutableLiveData<Int>(false.toVisibility())
    val noDataStubVisibility: LiveData<Int> get() = _noDataStubVisibility

    private val _noDataStubText = MutableLiveData<Int>()
    val noDataStubText: LiveData<Int> get() = _noDataStubText

    private val _noDataStubExplanation = MutableLiveData<Int>(if(model.isCurrentUser) R.string.no_followers_yet else R.string.no_followers_yet_external_user)
    val noDataStubExplanation: LiveData<Int> get() = _noDataStubExplanation

    val pageSize = model.pageSize

    private var hasFollowersData: Boolean? = null
    private var hasFollowingsData: Boolean? = null
    private var hasMutualData: Boolean? = null

    init {
        filter.observeForever {
            switchTab(filter.value!!)
            loadPage(it)
        }

        followersItems.observeForever {
            hasFollowersData = it.isNotEmpty()
            switchTab(filter.value!!)
        }

        followingsItems.observeForever {
            hasFollowingsData = it.isNotEmpty()
            switchTab(filter.value!!)
        }

        mutualsItems.observeForever {
            hasMutualData = it.isNotEmpty()
            switchTab(filter.value!!)
        }
    }

    private fun switchTab(filter: FollowersFilter) {
        when(filter) {
            FollowersFilter.FOLLOWERS ->
                hasFollowersData?.let {
                    if(it) {
                        _followersVisibility.value = it.toVisibility()
                        _followingsVisibility.value = (!it).toVisibility()
                        _mutualsVisibility.value = (!it).toVisibility()
                    }
                    _noDataStubVisibility.value = (!it).toVisibility()
                    _noDataStubText.value = R.string.no_followers
                }
            FollowersFilter.FOLLOWINGS ->
                hasFollowingsData?.let {
                    if(it) {
                        _followingsVisibility.value = it.toVisibility()
                        _followersVisibility.value = (!it).toVisibility()
                        _mutualsVisibility.value = (!it).toVisibility()
                    }
                    _noDataStubVisibility.value = (!it).toVisibility()
                    _noDataStubText.value = R.string.no_following
                }
            FollowersFilter.MUTUALS ->
                hasMutualData?.let {
                    if(it) {
                        _mutualsVisibility.value = it.toVisibility()
                        _followersVisibility.value = (!it).toVisibility()
                        _followingsVisibility.value = (!it).toVisibility()
                    }
                    _noDataStubVisibility.value = (!it).toVisibility()
                    _noDataStubText.value = R.string.no_mutual
                }
        }
    }

    override fun onNextPageReached(filter: FollowersFilter) = loadPage(filter)

    override fun retry(filter: FollowersFilter) {
        launch {
            model.retry(filter)
        }
    }

    override fun onJoinClick(userId: UserIdDomain, filter: FollowersFilter) {
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
