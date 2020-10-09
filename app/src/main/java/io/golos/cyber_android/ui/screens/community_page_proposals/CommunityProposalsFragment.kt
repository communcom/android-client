package io.golos.cyber_android.ui.screens.community_page_proposals

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import io.golos.cyber_android.R
import io.golos.cyber_android.application.App
import io.golos.cyber_android.databinding.FragmentCommunityProposalsBinding
import io.golos.cyber_android.ui.screens.community_page.view.CommunityPageFragment
import io.golos.cyber_android.ui.screens.community_page_proposals.adapter.ProposalsPagerAdapter
import io.golos.cyber_android.ui.screens.community_page_proposals.di.CommunityProposalsFragmentComponent
import io.golos.cyber_android.ui.screens.community_page_proposals.fragments.proposalsall.CommunityProposalsAllFragment
import io.golos.cyber_android.ui.screens.community_page_proposals.view_model.CommunityProposalsViewModel
import io.golos.cyber_android.ui.screens.profile.view.ProfileExternalUserFragment
import io.golos.cyber_android.ui.shared.mvvm.FragmentBaseMVVM
import io.golos.cyber_android.ui.shared.mvvm.view_commands.NavigateBackwardCommand
import io.golos.cyber_android.ui.shared.mvvm.view_commands.NavigateToCommunityPageCommand
import io.golos.cyber_android.ui.shared.mvvm.view_commands.NavigateToUserProfileCommand
import io.golos.cyber_android.ui.shared.mvvm.view_commands.ViewCommand
import io.golos.cyber_android.ui.shared.utils.TabLayoutMediator
import io.golos.domain.dto.CommunityIdDomain
import io.golos.domain.dto.UserIdDomain
import kotlinx.android.synthetic.main.fragment_community_proposals.*
import timber.log.Timber

class CommunityProposalsFragment:FragmentBaseMVVM<FragmentCommunityProposalsBinding, CommunityProposalsViewModel>(){

    private val TAG=CommunityProposalsFragment::class.java.simpleName
    companion object {

        private const val COMMUNITY_ID ="CommunityID"

        fun getInstance(communityIdDomain: CommunityIdDomain) = CommunityProposalsFragment().apply {
            arguments = Bundle().apply { putParcelable(COMMUNITY_ID, communityIdDomain) }
        }
    }

    override fun provideViewModelType(): Class<CommunityProposalsViewModel> =CommunityProposalsViewModel::class.java

    override fun layoutResId(): Int = R.layout.fragment_community_proposals

    override fun linkViewModel(binding: FragmentCommunityProposalsBinding, viewModel: CommunityProposalsViewModel) {
        val items = initPagerItems()
        binding.apply {
            vpProposals.adapter = ProposalsPagerAdapter(childFragmentManager, lifecycle, items)
            vpProposals.offscreenPageLimit = items.size
            TabLayoutMediator(proposalsTabs, vpProposals) { tab, position ->
                tab.text = items[position].title
                vpProposals.setCurrentItem(0, false)
            }.attach()
        }

    }
    override fun processViewCommand(command: ViewCommand) {
        super.processViewCommand(command)
        when (command) {
            is NavigateBackwardCommand -> requireFragmentManager().popBackStack()
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ivBack.setOnClickListener {
            viewModel.onBackPressed()
        }
    }
    override fun inject(key: String) {
        App.injections.get<CommunityProposalsFragmentComponent>(
            key,
            arguments?.getParcelable(COMMUNITY_ID)!!
        ).inject(this)
    }

    override fun releaseInjection(key: String) {
        App.injections.release<CommunityProposalsFragmentComponent>(key)
    }

    private fun initPagerItems():ArrayList<ProposalsPagerAdapter.FragmentPagerModel>{
        return arrayListOf(
            ProposalsPagerAdapter.FragmentPagerModel(CommunityProposalsAllFragment(),resources.getString(R.string.discovery_all))
        )
    }

    fun getProposalsPost(){
        viewModel.getProposal()
    }

    fun getProposalData()=viewModel.proposal
}