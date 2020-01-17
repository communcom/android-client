package io.golos.cyber_android.ui.screens.community_page_friends.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.golos.cyber_android.ui.screens.community_page_friends.model.CommunityPageFriendsModel
import io.golos.cyber_android.ui.screens.community_page_members.view.MembersListEventsProcessor
import io.golos.cyber_android.ui.shared.mvvm.viewModel.ViewModelBase
import io.golos.cyber_android.ui.shared.mvvm.view_commands.NavigateBackwardCommand
import io.golos.cyber_android.ui.shared.recycler_view.versioned.VersionedListItem
import io.golos.domain.DispatchersProvider
import io.golos.domain.dto.UserIdDomain
import kotlinx.coroutines.launch
import javax.inject.Inject

class CommunityPageFriendsViewModel
@Inject
constructor(
    dispatchersProvider: DispatchersProvider,
    model: CommunityPageFriendsModel
) : ViewModelBase<CommunityPageFriendsModel>(dispatchersProvider, model),
    MembersListEventsProcessor {

    @Suppress("LeakingThis")
    private val _title = MutableLiveData<String>(model.title)
    val title: LiveData<String> get() = _title

    val items: LiveData<List<VersionedListItem>> get() = model.getItems()

    init {
        launch {
            model.loadPage()
        }
    }

    override fun onUserClick(userId: UserIdDomain) {

    }

    fun onBackClick() {
        _command.value = NavigateBackwardCommand()
    }
}