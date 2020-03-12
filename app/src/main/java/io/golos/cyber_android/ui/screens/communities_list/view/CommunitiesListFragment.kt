package io.golos.cyber_android.ui.screens.communities_list.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import io.golos.cyber_android.R
import io.golos.cyber_android.application.App
import io.golos.cyber_android.databinding.FragmentCommunitiesBinding
import io.golos.cyber_android.ui.screens.communities_list.di.CommunitiesListFragmentComponent
import io.golos.cyber_android.ui.screens.communities_list.view.list.CommunityListAdapter
import io.golos.cyber_android.ui.screens.communities_list.view_model.CommunitiesListViewModel
import io.golos.cyber_android.ui.screens.community_page.view.CommunityPageFragment
import io.golos.cyber_android.ui.shared.mvvm.FragmentBaseMVVM
import io.golos.cyber_android.ui.shared.mvvm.view_commands.NavigateBackwardCommand
import io.golos.cyber_android.ui.shared.mvvm.view_commands.NavigateToCommunityPageCommand
import io.golos.cyber_android.ui.shared.mvvm.view_commands.ViewCommand
import io.golos.cyber_android.ui.shared.recycler_view.versioned.VersionedListItem
import io.golos.cyber_android.ui.shared.utils.CommunitiesListDividerDecoration
import io.golos.domain.dto.UserIdDomain
import kotlinx.android.synthetic.main.fragment_communities.*

open class CommunitiesListFragment : FragmentBaseMVVM<FragmentCommunitiesBinding, CommunitiesListViewModel>() {
    companion object {
        private const val USER_ID = "USER_ID"

        fun newInstance(userId: UserIdDomain) = CommunitiesListFragment().apply {
            arguments = Bundle().apply {
                putParcelable(USER_ID, userId)
            }
        }
    }

    private lateinit var communitiesListAdapter: CommunityListAdapter
    private lateinit var communitiesListLayoutManager: LinearLayoutManager

    override fun provideViewModelType(): Class<CommunitiesListViewModel> = CommunitiesListViewModel::class.java

    override fun layoutResId(): Int = R.layout.fragment_communities

    override fun inject(key: String) = App
        .injections.get<CommunitiesListFragmentComponent>(
            key,
            true,                                                   // show back button
            arguments!!.getParcelable<UserIdDomain>(USER_ID),       // user id
            false)                                                  // show all posts
        .inject(this)

    override fun releaseInjection(key: String) = App.injections.release<CommunitiesListFragmentComponent>(key)

    override fun linkViewModel(binding: FragmentCommunitiesBinding, viewModel: CommunitiesListViewModel) {
        binding.viewModel = viewModel
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        with(viewModel) {
            items.observe({
                viewLifecycleOwner.lifecycle
            }) {
                updateList(it)
            }
        }

        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.onViewCreated()
    }

    override fun processViewCommand(command: ViewCommand) {
        super.processViewCommand(command)

        when (command) {
            is NavigateToCommunityPageCommand -> moveToCommunityPage(command.communityId)
            is NavigateBackwardCommand -> requireActivity().onBackPressed()
        }
    }

    private fun updateList(data: List<VersionedListItem>) {
        if (!::communitiesListAdapter.isInitialized) {
            communitiesListLayoutManager = LinearLayoutManager(context)

            communitiesListAdapter = CommunityListAdapter(viewModel, viewModel.pageSize)
            communitiesListAdapter.setHasStableIds(true)

            mainList.isSaveEnabled = false
            mainList.itemAnimator = null
            mainList.layoutManager = communitiesListLayoutManager
            mainList.adapter = communitiesListAdapter
            mainList.addItemDecoration(CommunitiesListDividerDecoration(requireContext()))
        }

        communitiesListAdapter.update(data)
    }

    private fun moveToCommunityPage(communityId: String) =
        getDashboardFragment(this)?.navigateToFragment(CommunityPageFragment.newInstance(communityId))
}