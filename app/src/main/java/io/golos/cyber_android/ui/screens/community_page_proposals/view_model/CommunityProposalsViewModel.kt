package io.golos.cyber_android.ui.screens.community_page_proposals.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.golos.cyber_android.ui.screens.community_page_proposals.model.CommunityProposalsModel
import io.golos.cyber_android.ui.shared.mvvm.viewModel.ViewModelBase
import io.golos.cyber_android.ui.shared.mvvm.view_commands.NavigateBackwardCommand
import io.golos.domain.DispatchersProvider
import io.golos.domain.dto.ProposalDomain
import io.golos.domain.dto.ReportedPostDomain
import kotlinx.coroutines.launch
import timber.log.Timber
import java.lang.Exception
import javax.inject.Inject

class CommunityProposalsViewModel
@Inject
constructor(
    model:CommunityProposalsModel,
    dispatchersProvider: DispatchersProvider
):ViewModelBase<CommunityProposalsModel>(
    dispatchersProvider,
    model
){
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
}