package io.golos.cyber_android.ui.screens.community_page_proposals.fragments.proposalsall

import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import io.golos.cyber_android.R
import io.golos.cyber_android.application.App
import io.golos.cyber_android.databinding.FragmentCommunityProposalsAllBinding
import io.golos.cyber_android.ui.screens.community_page.view.CommunityPageFragment
import io.golos.cyber_android.ui.screens.community_page_proposals.fragments.proposalsall.adapter.AllProposalsAdapter
import io.golos.cyber_android.ui.screens.community_page_proposals.fragments.proposalsall.di.CommunityAllProposalsFragmentComponent
import io.golos.cyber_android.ui.screens.profile.view.ProfileExternalUserFragment
import io.golos.cyber_android.ui.shared.mvvm.FragmentBaseMVVM
import io.golos.cyber_android.ui.shared.mvvm.view_commands.NavigateToCommunityPageCommand
import io.golos.cyber_android.ui.shared.mvvm.view_commands.NavigateToUserProfileCommand
import io.golos.cyber_android.ui.shared.mvvm.view_commands.ViewCommand
import io.golos.domain.dto.CommunityIdDomain
import io.golos.domain.dto.ProposalDomain
import io.golos.domain.dto.UserIdDomain
import kotlinx.android.synthetic.main.fragment_community_proposals_all.*
import timber.log.Timber


class CommunityProposalsAllFragment : FragmentBaseMVVM<FragmentCommunityProposalsAllBinding, CommunityAllProposalsViewModel>() {
    private val TAG = CommunityProposalsAllFragment::class.java.simpleName

    override fun provideViewModelType(): Class<CommunityAllProposalsViewModel> = CommunityAllProposalsViewModel::class.java

    override fun layoutResId(): Int = R.layout.fragment_community_proposals_all

    override fun linkViewModel(binding: FragmentCommunityProposalsAllBinding, viewModel: CommunityAllProposalsViewModel) {
        viewModel.getProposal()
        viewModel.proposal.observe(viewLifecycleOwner, Observer {
            updateList(it,viewModel)
            Timber.i(TAG, "linkViewModel: ${it[0].proposer?.username}")
        })
    }

    override fun inject(key: String) {
        App.injections.get<CommunityAllProposalsFragmentComponent>(key).inject(this)
    }

    override fun releaseInjection(key: String) {
        App.injections.release<CommunityAllProposalsFragmentComponent>(key)
    }

    override fun processViewCommand(command: ViewCommand) {
        super.processViewCommand(command)
        when (command) {
            is NavigateToUserProfileCommand -> openUserProfile(command.userId)
            is NavigateToCommunityPageCommand -> openCommunityPage(command.communityId)
        }
    }
    private fun openUserProfile(userId: UserIdDomain) {
        getDashboardFragment(this)?.navigateToFragment(ProfileExternalUserFragment.newInstance(userId))
    }
    private fun openCommunityPage(communityId: CommunityIdDomain) {
        getDashboardFragment(this)?.navigateToFragment(CommunityPageFragment.newInstance(communityId))
    }
    fun updateList(data: List<ProposalDomain>, viewModel: CommunityAllProposalsViewModel) {

        val communitiesListLayoutManager = LinearLayoutManager(context)
        val allProposalsListAdapter = AllProposalsAdapter(data,viewModel)
        allProposalsListAdapter.setHasStableIds(true)
        rvProposals.layoutManager = communitiesListLayoutManager
        rvProposals.adapter = allProposalsListAdapter

    }

}