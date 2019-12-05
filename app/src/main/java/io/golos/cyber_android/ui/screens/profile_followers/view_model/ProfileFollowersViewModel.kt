package io.golos.cyber_android.ui.screens.profile_followers.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.golos.cyber_android.ui.common.mvvm.viewModel.ViewModelBase
import io.golos.cyber_android.ui.dto.FollowersFilter
import io.golos.cyber_android.ui.screens.profile_followers.model.ProfileFollowersModel
import io.golos.cyber_android.ui.screens.profile_followers.view.list.FollowersListItemEventsProcessor
import io.golos.domain.DispatchersProvider
import io.golos.domain.repositories.CurrentUserRepositoryRead
import javax.inject.Inject

class ProfileFollowersViewModel
@Inject
constructor(
    startFilter: FollowersFilter,
    private val dispatchersProvider: DispatchersProvider,
    model: ProfileFollowersModel,
    currentUserRepository: CurrentUserRepositoryRead
) : ViewModelBase<ProfileFollowersModel>(dispatchersProvider, model),
    FollowersListItemEventsProcessor {

    private val _title = MutableLiveData<String>(currentUserRepository.userName)
    val title: LiveData<String> get() = _title

    val filter = MutableLiveData<FollowersFilter>(startFilter)
}
