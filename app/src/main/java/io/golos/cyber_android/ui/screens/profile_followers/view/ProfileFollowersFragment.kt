package io.golos.cyber_android.ui.screens.profile_followers.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.golos.cyber_android.R
import io.golos.cyber_android.application.App
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.profile_fragment.profile_followers.ProfileFollowersFragmentComponent
import io.golos.cyber_android.databinding.FragmentProfileFollowersBinding
import io.golos.cyber_android.ui.common.mvvm.FragmentBaseMVVM
import io.golos.cyber_android.ui.dto.FollowersFilter
import io.golos.cyber_android.ui.screens.profile_followers.view_model.ProfileFollowersViewModel

class ProfileFollowersFragment : FragmentBaseMVVM<FragmentProfileFollowersBinding, ProfileFollowersViewModel>() {
    companion object {
        private const val FILTER = "FILTER"

        fun newInstance(filter: FollowersFilter) = ProfileFollowersFragment().apply {
            arguments = Bundle().apply {
                putInt(FILTER, filter.value)
            }
        }
    }

//    private lateinit var communitiesListAdapter: CommunityListAdapter
//    private lateinit var communitiesListLayoutManager: LinearLayoutManager

    override fun provideViewModelType(): Class<ProfileFollowersViewModel> = ProfileFollowersViewModel::class.java

    override fun layoutResId(): Int = R.layout.fragment_profile_followers

    override fun inject() =
        App.injections.get<ProfileFollowersFragmentComponent>(FollowersFilter.create(arguments!!.getInt(FILTER))).inject(this)

    override fun releaseInjection() {
        App.injections.release<ProfileFollowersFragmentComponent>()
    }

    override fun linkViewModel(binding: FragmentProfileFollowersBinding, viewModel: ProfileFollowersViewModel) {
        binding.viewModel = viewModel
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
//        with(viewModel) {
//            items.observe({viewLifecycleOwner.lifecycle}) { updateList(it) }
//        }

        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        viewModel.onViewCreated()
    }

//    override fun processViewCommand(command: ViewCommand) {
//        when(command) {
//            is NavigateToCommunityPageCommand -> moveToCommunity(command.communityId)
//            is NavigateToCommunitiesListPageCommand -> moveToCommunitiesList()
//        }
//    }

//    private fun updateList(data: List<VersionedListItem>) {
//        if(!::communitiesListAdapter.isInitialized) {
//            communitiesListLayoutManager = LinearLayoutManager(context)
//            communitiesListLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
//
//            communitiesListAdapter = CommunityListAdapter(viewModel)
//            communitiesListAdapter.setHasStableIds(true)
//
//            communitiesList.isSaveEnabled = false
//            communitiesList.itemAnimator = null
//            communitiesList.layoutManager = communitiesListLayoutManager
//            communitiesList.adapter = communitiesListAdapter
//        }
//
//        communitiesListAdapter.update(data)
//    }

//    private fun moveToCommunity(communityId: String) =
//        (requireActivity() as? MainActivity)?.showFragment(CommunityPageFragment.newInstance(communityId))
//
//    private fun moveToCommunitiesList() =
//        (requireActivity() as? MainActivity)?.showFragment(CommunitiesListFragment.newInstance())
}