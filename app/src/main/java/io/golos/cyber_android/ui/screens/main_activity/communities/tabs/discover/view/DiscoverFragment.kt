package io.golos.cyber_android.ui.screens.main_activity.communities.tabs.discover.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import io.golos.cyber_android.R
import io.golos.cyber_android.application.App
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.main_activity.communities_fragment.discover_fragment.DiscoverFragmentComponent
import io.golos.cyber_android.databinding.FragmentDiscoverBinding
import io.golos.cyber_android.ui.common.mvvm.FragmentBaseMVVM
import io.golos.cyber_android.ui.common.recycler_view.ListItem
import io.golos.cyber_android.ui.screens.main_activity.communities.search_bridge.ChildSearchFragment
import io.golos.cyber_android.ui.screens.main_activity.communities.search_bridge.SearchBridgeChild
import io.golos.cyber_android.ui.screens.main_activity.communities.tabs.discover.model.DiscoverModel
import io.golos.cyber_android.ui.screens.main_activity.communities.tabs.discover.view.list.CommunityListAdapter
import io.golos.cyber_android.ui.screens.main_activity.communities.tabs.discover.view.list.CommunityListScrollListener
import io.golos.cyber_android.ui.screens.main_activity.communities.tabs.discover.viewModel.DiscoverViewModel
import io.golos.domain.AppResourcesProvider
import kotlinx.android.synthetic.main.fragment_discover.*
import javax.inject.Inject

class DiscoverFragment : FragmentBaseMVVM<FragmentDiscoverBinding, DiscoverModel, DiscoverViewModel>(), ChildSearchFragment {
    companion object {
        fun newInstance() = DiscoverFragment()
    }

    private lateinit var communitiesListAdapter: CommunityListAdapter
    private lateinit var communitiesListLayoutManager: LinearLayoutManager
    private var communitiesScrollListener: CommunityListScrollListener? = null

    private lateinit var searchListAdapter: CommunityListAdapter
    private lateinit var searchListLayoutManager: LinearLayoutManager

    @Inject
    internal lateinit var appResources: AppResourcesProvider

    @Inject
    internal lateinit var searchBridge: SearchBridgeChild

    override fun provideViewModelType(): Class<DiscoverViewModel> = DiscoverViewModel::class.java

    override fun provideLayout(): Int = R.layout.fragment_discover

    override fun inject() = App.injections.get<DiscoverFragmentComponent>().inject(this)

    override fun releaseInjection() {
        App.injections.release<DiscoverFragmentComponent>()
    }

    override fun linkViewModel(binding: FragmentDiscoverBinding, viewModel: DiscoverViewModel) {
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

        val labelLayoutParams = recomendedText.layoutParams as ConstraintLayout.LayoutParams
        val labelTopMargin = if(isVisible) appResources.getDimens(R.dimen.margin_vertical_default_16dp).toInt() else 0
        labelLayoutParams.setMargins(0, labelTopMargin, 0, 0)
        recomendedText.layoutParams = labelLayoutParams
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