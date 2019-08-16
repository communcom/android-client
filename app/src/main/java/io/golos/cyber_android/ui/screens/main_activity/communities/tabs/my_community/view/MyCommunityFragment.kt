package io.golos.cyber_android.ui.screens.main_activity.communities.tabs.my_community.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import io.golos.cyber_android.R
import io.golos.cyber_android.application.App
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.main_activity.communities_fragment.my_community_fragment.MyCommunityFragmentComponent
import io.golos.cyber_android.databinding.FragmentMyCommunitiesBinding
import io.golos.cyber_android.ui.common.mvvm.FragmentBaseMVVM
import io.golos.cyber_android.ui.common.recycler_view.ListItem
import io.golos.cyber_android.ui.screens.main_activity.communities.search_bridge.ChildSearchFragment
import io.golos.cyber_android.ui.screens.main_activity.communities.search_bridge.SearchBridgeChild
import io.golos.cyber_android.ui.screens.main_activity.communities.tabs.common.model.CommunityModel
import io.golos.cyber_android.ui.screens.main_activity.communities.tabs.common.view.list.CommunityListAdapter
import io.golos.cyber_android.ui.screens.main_activity.communities.tabs.common.view.list.CommunityListScrollListener
import io.golos.cyber_android.ui.screens.main_activity.communities.tabs.common.viewModel.CommunityViewModel
import kotlinx.android.synthetic.main.fragment_my_communities.*
import javax.inject.Inject

class MyCommunityFragment : FragmentBaseMVVM<FragmentMyCommunitiesBinding, CommunityModel, CommunityViewModel>(), ChildSearchFragment {
    companion object {
        fun newInstance() = MyCommunityFragment()
    }

    private lateinit var communitiesListAdapter: CommunityListAdapter
    private lateinit var communitiesListLayoutManager: LinearLayoutManager
    private var communitiesScrollListener: CommunityListScrollListener? = null

    private lateinit var searchListAdapter: CommunityListAdapter
    private lateinit var searchListLayoutManager: LinearLayoutManager

    @Inject
    internal lateinit var searchBridge: SearchBridgeChild

    override fun provideViewModelType(): Class<CommunityViewModel> = CommunityViewModel::class.java

    override fun provideLayout(): Int = R.layout.fragment_my_communities

    override fun inject() = App.injections.get<MyCommunityFragmentComponent>().inject(this)

    override fun releaseInjection() {
        App.injections.release<MyCommunityFragmentComponent>()
    }

    override fun linkViewModel(binding: FragmentMyCommunitiesBinding, viewModel: CommunityViewModel) {
        binding.viewModel = viewModel
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        with(viewModel) {
            searchResultVisibility.observe({viewLifecycleOwner.lifecycle}) { updateSearchResultVisibility(it) }

            items.observe({viewLifecycleOwner.lifecycle}) { updateList(it) }
            searchResultItems.observe({viewLifecycleOwner.lifecycle}) { updateSearchList(it) }

            isScrollEnabled.observe({viewLifecycleOwner.lifecycle}) { setScrollState(it) }
        }

        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.onViewCreated()
    }

    override fun onResume() {
        super.onResume()
        viewModel.onActive(root.height)
        searchBridge.getParent().setSearchString(viewModel.searchString)
    }

    override fun onSearchStringUpdate(searchString: String) {
        viewModel.onSearchStringUpdated(searchString)
    }

    private fun updateSearchResultVisibility(isVisible: Boolean) {
        searchResultList.visibility = if(isVisible) View.VISIBLE else View.GONE
    }

    private fun updateList(data: List<ListItem>) {
        if(!::communitiesListAdapter.isInitialized) {
            communitiesListLayoutManager = LinearLayoutManager(context)

            communitiesListAdapter = CommunityListAdapter(viewModel)
            communitiesListAdapter.setHasStableIds(true)

            mainList.isSaveEnabled = false
            mainList.itemAnimator = null
            mainList.layoutManager = communitiesListLayoutManager
            mainList.adapter = communitiesListAdapter
        }

        communitiesListAdapter.update(data)
    }

    private fun updateSearchList(data: List<ListItem>) {
        if(!::searchListAdapter.isInitialized) {
            searchListLayoutManager = LinearLayoutManager(context)

            searchListAdapter = CommunityListAdapter(viewModel)
            searchListAdapter.setHasStableIds(true)

            searchResultList.isSaveEnabled = false
            searchResultList.itemAnimator = null
            searchResultList.layoutManager = searchListLayoutManager
            searchResultList.adapter = searchListAdapter
        }

        searchListAdapter.update(data)
    }

    private fun setScrollState(isScrollEnabled: Boolean) {
        if(!::communitiesListAdapter.isInitialized) {
            return
        }

        communitiesScrollListener?.let { mainList.removeOnScrollListener(it) }

        if(isScrollEnabled) {
            mainList.addOnScrollListener(CommunityListScrollListener(communitiesListLayoutManager) { viewModel.onScroll(it) })
        }
    }
}