package io.golos.cyber_android.ui.screens.main_activity.communities.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import io.golos.cyber_android.R
import io.golos.cyber_android.application.App
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.main_activity.communities_fragment.CommunitiesFragmentComponent
import io.golos.cyber_android.databinding.FragmentCommunitiesBinding
import io.golos.cyber_android.ui.common.mvvm.FragmentBaseMVVM
import io.golos.cyber_android.ui.common.mvvm.view_commands.NavigateToCommunityPageCommand
import io.golos.cyber_android.ui.common.mvvm.view_commands.ViewCommand
import io.golos.cyber_android.ui.common.recycler_view.versioned.VersionedListItem
import io.golos.cyber_android.ui.screens.community_page.CommunityPageFragment
import io.golos.cyber_android.ui.screens.main_activity.MainActivity
import io.golos.cyber_android.ui.screens.main_activity.communities.view.list.CommunityListAdapter
import io.golos.cyber_android.ui.screens.main_activity.communities.view_model.CommunitiesViewModel
import kotlinx.android.synthetic.main.fragment_communities.*

class CommunitiesFragment : FragmentBaseMVVM<FragmentCommunitiesBinding, CommunitiesViewModel>() {
    companion object {
        fun newInstance() = CommunitiesFragment()    }

    private lateinit var communitiesListAdapter: CommunityListAdapter
    private lateinit var communitiesListLayoutManager: LinearLayoutManager

    override fun provideViewModelType(): Class<CommunitiesViewModel> = CommunitiesViewModel::class.java

    override fun layoutResId(): Int = R.layout.fragment_communities

    override fun inject() = App.injections.get<CommunitiesFragmentComponent>().inject(this)

    override fun releaseInjection() {
        App.injections.release<CommunitiesFragmentComponent>()
    }

    override fun linkViewModel(binding: FragmentCommunitiesBinding, viewModel: CommunitiesViewModel) {
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
        super.processViewCommand(command)
        if(command is NavigateToCommunityPageCommand){
            val requireActivity = requireActivity()
            if(requireActivity is MainActivity){
                requireActivity.showFragment(CommunityPageFragment.newInstance(command.communityId))
            }
        }
    }

    private fun updateList(data: List<VersionedListItem>) {
        if(!::communitiesListAdapter.isInitialized) {
            communitiesListLayoutManager = LinearLayoutManager(context)

            communitiesListAdapter = CommunityListAdapter(viewModel, viewModel.pageSize)
            communitiesListAdapter.setHasStableIds(true)

            mainList.isSaveEnabled = false
            mainList.itemAnimator = null
            mainList.layoutManager = communitiesListLayoutManager
            mainList.adapter = communitiesListAdapter
        }

        communitiesListAdapter.update(data)
    }
}