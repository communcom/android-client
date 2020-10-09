package io.golos.cyber_android.ui.screens.community_page_proposals.fragments.proposalsall

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.screens.community_page_proposals.model.CommunityProposalsModel
import io.golos.cyber_android.ui.shared.mvvm.viewModel.ViewModelBase
import io.golos.cyber_android.ui.shared.mvvm.view_commands.*
import io.golos.domain.DispatchersProvider
import io.golos.domain.dto.CommunityIdDomain
import io.golos.domain.dto.ProposalDomain
import io.golos.domain.dto.ReportedPostDomain
import io.golos.domain.dto.UserIdDomain
import io.golos.domain.repositories.exceptions.ApiResponseErrorException
import kotlinx.coroutines.launch
import timber.log.Timber
import java.lang.Exception
import javax.inject.Inject

class CommunityAllProposalsViewModel
@Inject constructor(model: CommunityProposalsModel, dispatchersProvider: DispatchersProvider) : ViewModelBase<CommunityProposalsModel>(dispatchersProvider, model) {
    private val _proposal = MutableLiveData<List<ProposalDomain>>()
    val proposal: LiveData<List<ProposalDomain>>
        get() = _proposal

    fun getProposal() {
        launch {
            try {
                val a = model.getProposals(40, 0)
                _proposal.postValue(a)
            } catch (e: Exception) {
                Timber.e(e)
            }
        }
    }

    fun onBackPressed() {
        _command.value = NavigateBackwardCommand()
    }

    fun onUserInHeaderClick(userId: String) {
        launch {
            try {
                _command.value = NavigateToUserProfileCommand(UserIdDomain(userId))
            } catch (ex: Exception) {
                Timber.e(ex)
                _command.value = if (ex is ApiResponseErrorException && ex.message != null) {
                    ShowMessageTextCommand(ex.message!!)
                } else {
                    ShowMessageResCommand(R.string.common_general_error)
                }
            }
        }
    }

    fun onCommunityClicked(communityId: CommunityIdDomain) {
        _command.value = NavigateToCommunityPageCommand(communityId)
    }
}