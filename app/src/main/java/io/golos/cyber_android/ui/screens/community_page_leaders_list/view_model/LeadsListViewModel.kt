package io.golos.cyber_android.ui.screens.community_page_leaders_list.view_model

import androidx.lifecycle.LiveData
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.shared.mvvm.viewModel.ViewModelBase
import io.golos.cyber_android.ui.shared.mvvm.view_commands.ShowMessageResCommand
import io.golos.cyber_android.ui.shared.recycler_view.versioned.VersionedListItem
import io.golos.cyber_android.ui.screens.community_page_leaders_list.dto.VoteResult
import io.golos.cyber_android.ui.screens.community_page_leaders_list.dto.VoteResultType
import io.golos.cyber_android.ui.screens.community_page_leaders_list.model.LeadsListModel
import io.golos.cyber_android.ui.screens.community_page_leaders_list.view.list.LeadsListItemEventsProcessor
import io.golos.cyber_android.ui.shared.mvvm.view_commands.ShowMessageTextCommand
import io.golos.domain.DispatchersProvider
import io.golos.domain.dto.UserIdDomain
import kotlinx.coroutines.launch
import javax.inject.Inject

class LeadsListViewModel
@Inject
constructor(
    dispatchersProvider: DispatchersProvider,
    model: LeadsListModel
) : ViewModelBase<LeadsListModel>(dispatchersProvider, model),
    LeadsListItemEventsProcessor {

    val items: LiveData<List<VersionedListItem>>
        get()  = model.items

    fun onViewCreated() {
        launch {
            model.loadLeaders()
        }
    }

    override fun retry() {
        launch {
            model.retry()
        }
    }

    override fun vote(leader: UserIdDomain) {
        launch {
            processVoteResult(model.vote(leader))
        }
    }

    override fun unvote(leader: UserIdDomain) {
        launch {
            processVoteResult(model.unvote(leader))
        }
    }

    private fun processVoteResult(voteResult: VoteResult) =
        when(voteResult.type) {
            VoteResultType.FAIL -> _command.value =
                voteResult.message
                    ?.let {
                        ShowMessageTextCommand(it)
                    }
                    ?: ShowMessageResCommand(R.string.common_general_error)

            VoteResultType.VOTE_IN_PROGRESS -> _command.value = ShowMessageResCommand(R.string.voting_in_progress)
            VoteResultType.UNVOTE_NEEDED -> _command.value = ShowMessageResCommand(R.string.voting_one_leader)
            else -> {}
        }
}