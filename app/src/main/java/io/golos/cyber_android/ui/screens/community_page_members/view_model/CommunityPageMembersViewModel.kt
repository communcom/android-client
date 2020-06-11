package io.golos.cyber_android.ui.screens.community_page_members.view_model

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.screens.community_page_members.model.CommunityPageMembersModel
import io.golos.cyber_android.ui.screens.community_page_members.view.MembersListEventsProcessor
import io.golos.cyber_android.ui.shared.mvvm.viewModel.ViewModelBase
import io.golos.cyber_android.ui.shared.mvvm.view_commands.NavigateBackwardCommand
import io.golos.cyber_android.ui.shared.mvvm.view_commands.NavigateToUserProfileCommand
import io.golos.cyber_android.ui.shared.mvvm.view_commands.ShowMessageResCommand
import io.golos.cyber_android.ui.shared.mvvm.view_commands.ShowMessageTextCommand
import io.golos.cyber_android.ui.shared.recycler_view.versioned.VersionedListItem
import io.golos.domain.DispatchersProvider
import io.golos.domain.dto.UserIdDomain
import kotlinx.coroutines.launch
import javax.inject.Inject

class CommunityPageMembersViewModel
@Inject
constructor(
    dispatchersProvider: DispatchersProvider,
    model: CommunityPageMembersModel
) : ViewModelBase<CommunityPageMembersModel>(dispatchersProvider, model),
    MembersListEventsProcessor {

    val pageSize: Int get() = model.pageSize

    @Suppress("LeakingThis")
    private val _title = MutableLiveData<String>(this.model.title)
    val title: LiveData<String> get() = _title

    val items: LiveData<List<VersionedListItem>> get() = model.getItems()

    private val _itemsVisibility = MutableLiveData<Int>(true.toVisibility())
    val itemsVisibility: LiveData<Int> get() = _itemsVisibility

    private val _noDataStubVisibility = MutableLiveData<Int>(false.toVisibility())
    val noDataStubVisibility: LiveData<Int> get() = _noDataStubVisibility

    private val _noDataStubText = MutableLiveData<Int>(this.model.noDataStubText)
    val noDataStubText: LiveData<Int> get() = _noDataStubText

    private val _noDataStubExplanation = MutableLiveData<Int>(this.model.noDataStubExplanation)
    val noDataStubExplanation: LiveData<Int> get() = _noDataStubExplanation

    init {
        items.observeForever {
            _noDataStubVisibility.value = it.isEmpty().toVisibility()
            _itemsVisibility.value = it.isNotEmpty().toVisibility()

        }

        loadPage()
    }

    override fun onNextPageReached() = loadPage()

    override fun retry() {
        launch {
            model.retry()
        }
    }

    override fun onFollowClick(userId: UserIdDomain) {
        launch {
            model.subscribeUnsubscribe(userId)?.let {
                _command.value = ShowMessageTextCommand(it.message)
            }
        }
    }

    override fun onUserClick(userId: UserIdDomain) {
        _command.value = NavigateToUserProfileCommand(userId)
    }

    fun onBackClick() {
        _command.value = NavigateBackwardCommand()
    }

    private fun Boolean.toVisibility() = if(this) View.VISIBLE else View.INVISIBLE

    private fun loadPage() {
        launch {
            model.loadPage()
        }
    }
}
