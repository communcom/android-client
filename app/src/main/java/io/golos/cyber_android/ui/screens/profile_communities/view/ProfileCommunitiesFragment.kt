package io.golos.cyber_android.ui.screens.profile_communities.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import io.golos.cyber_android.R
import io.golos.cyber_android.application.App
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.profile_fragment.profile_communities.ProfileCommunitiesFragmentComponent
import io.golos.cyber_android.databinding.FragmentProfileCommunitiesBinding
import io.golos.cyber_android.ui.common.mvvm.FragmentBaseMVVM
import io.golos.cyber_android.ui.common.mvvm.view_commands.NavigateToCommunitiesListPageCommand
import io.golos.cyber_android.ui.common.mvvm.view_commands.NavigateToCommunityPageCommand
import io.golos.cyber_android.ui.common.mvvm.view_commands.ViewCommand
import io.golos.cyber_android.ui.common.recycler_view.versioned.VersionedListItem
import io.golos.cyber_android.ui.dto.ProfileCommunities
import io.golos.cyber_android.ui.screens.communities_list.view.CommunitiesListFragment
import io.golos.cyber_android.ui.screens.community_page.view.CommunityPageFragment
import io.golos.cyber_android.ui.screens.main_activity.MainActivity
import io.golos.cyber_android.ui.screens.profile_communities.view.list.CommunityListAdapter
import io.golos.cyber_android.ui.screens.profile_communities.view_model.ProfileCommunitiesViewModel
import kotlinx.android.synthetic.main.fragment_profile_communities.*

class ProfileCommunitiesFragment : FragmentBaseMVVM<FragmentProfileCommunitiesBinding, ProfileCommunitiesViewModel>() {
    companion object {
        private const val SOURCE_DATA = "SOURCE_DATA"

        fun newInstance(sourceData: ProfileCommunities) =
            ProfileCommunitiesFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(SOURCE_DATA, sourceData)
                }
            }
    }

    private lateinit var communitiesListAdapter: CommunityListAdapter
    private lateinit var communitiesListLayoutManager: LinearLayoutManager

    override fun provideViewModelType(): Class<ProfileCommunitiesViewModel> = ProfileCommunitiesViewModel::class.java

    override fun layoutResId(): Int = R.layout.fragment_profile_communities

    override fun inject() =
        App.injections
        .get<ProfileCommunitiesFragmentComponent>(arguments!!.getParcelable<ProfileCommunities>(SOURCE_DATA))
        .inject(this)

    override fun releaseInjection() {
        App.injections.release<ProfileCommunitiesFragmentComponent>()
    }

    override fun linkViewModel(binding: FragmentProfileCommunitiesBinding, viewModel: ProfileCommunitiesViewModel) {
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
            is NavigateToCommunityPageCommand -> moveToCommunity(command.communityId)
            is NavigateToCommunitiesListPageCommand -> moveToCommunitiesList()
        }
    }

    private fun updateList(data: List<VersionedListItem>) {
        if(!::communitiesListAdapter.isInitialized) {
            communitiesListLayoutManager = LinearLayoutManager(context)
            communitiesListLayoutManager.orientation = LinearLayoutManager.HORIZONTAL

            communitiesListAdapter = CommunityListAdapter(viewModel)
            communitiesListAdapter.setHasStableIds(true)

            communitiesList.isSaveEnabled = false
            communitiesList.itemAnimator = null
            communitiesList.layoutManager = communitiesListLayoutManager
            communitiesList.adapter = communitiesListAdapter
        }

        communitiesListAdapter.update(data)
    }

    private fun moveToCommunity(communityId: String) =
        (requireActivity() as? MainActivity)?.showFragment(CommunityPageFragment.newInstance(communityId))

    private fun moveToCommunitiesList() =
        (requireActivity() as? MainActivity)?.showFragment(CommunitiesListFragment.newInstance())
}