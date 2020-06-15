package io.golos.cyber_android.ui.screens.community_page_leaders_list.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import io.golos.cyber_android.R
import io.golos.cyber_android.application.App
import io.golos.cyber_android.ui.screens.community_page_leaders_list.di.CommunityPageLeadsListComponent
import io.golos.cyber_android.databinding.FragmentCommunityLeadsBinding
import io.golos.cyber_android.ui.shared.mvvm.FragmentBaseMVVM
import io.golos.cyber_android.ui.shared.recycler_view.versioned.VersionedListItem
import io.golos.cyber_android.ui.screens.community_page_leaders_list.view.list.LeadsListListAdapter
import io.golos.cyber_android.ui.screens.community_page_leaders_list.view_model.LeadsListViewModel
import io.golos.cyber_android.ui.screens.profile.view.ProfileExternalUserFragment
import io.golos.cyber_android.ui.shared.mvvm.view_commands.NavigateToUserProfileCommand
import io.golos.cyber_android.ui.shared.mvvm.view_commands.ViewCommand
import io.golos.domain.dto.CommunityIdDomain
import io.golos.domain.dto.UserIdDomain
import io.golos.utils.helpers.EMPTY
import kotlinx.android.synthetic.main.fragment_community_leads.*

class LeadsListFragment : FragmentBaseMVVM<FragmentCommunityLeadsBinding, LeadsListViewModel>() {
    companion object {
        private const val ARG_COMMUNITY_ID = "ARG_COMMUNITY_ID"

        fun newInstance(communityId: CommunityIdDomain): LeadsListFragment =
            LeadsListFragment()
                .apply {
                    arguments = Bundle().apply { putParcelable(ARG_COMMUNITY_ID, communityId) }
                }
    }

    private lateinit var leadsListListAdapter: LeadsListListAdapter
    private lateinit var leadsListLayoutManager: LinearLayoutManager

    override fun provideViewModelType(): Class<LeadsListViewModel> = LeadsListViewModel::class.java

    override fun layoutResId(): Int = R.layout.fragment_community_leads

    override fun inject(key: String) = App.injections.get<CommunityPageLeadsListComponent>(key).inject(this)

    override fun releaseInjection(key: String) = App.injections.release<CommunityPageLeadsListComponent>(key)

    override fun linkViewModel(binding: FragmentCommunityLeadsBinding, viewModel: LeadsListViewModel) {
        binding.viewModel = viewModel
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        with(viewModel) {
            items.observe({viewLifecycleOwner.lifecycle}) { updateList(it) }
        }

        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.onViewCreated()
    }

    override fun processViewCommand(command: ViewCommand) {
        when(command) {
            is NavigateToUserProfileCommand -> navigateToUserProfile(command.userId)
        }
    }

    private fun updateList(data: List<VersionedListItem>) {
        if(!::leadsListListAdapter.isInitialized) {
            leadsListLayoutManager = LinearLayoutManager(context)

            leadsListListAdapter = LeadsListListAdapter(viewModel)
            leadsListListAdapter.setHasStableIds(true)

            mainList.isSaveEnabled = false
            mainList.itemAnimator = null
            mainList.layoutManager = leadsListLayoutManager
            mainList.adapter = leadsListListAdapter
        }

        leadsListListAdapter.update(data)
    }

    private fun navigateToUserProfile(userId: UserIdDomain) {
        getDashboardFragment(this)?.navigateToFragment(ProfileExternalUserFragment.newInstance(userId))
    }
}