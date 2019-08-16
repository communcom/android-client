package io.golos.cyber_android.ui.screens.main_activity.communities.tabs.common.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.golos.cyber_android.ui.common.mvvm.FragmentBaseMVVM
import io.golos.cyber_android.ui.common.recycler_view.ListItem
import io.golos.cyber_android.ui.screens.main_activity.communities.search_bridge.ChildSearchFragment
import io.golos.cyber_android.ui.screens.main_activity.communities.search_bridge.SearchBridgeChild
import io.golos.cyber_android.ui.screens.main_activity.communities.tabs.common.model.CommunityModel
import io.golos.cyber_android.ui.screens.main_activity.communities.tabs.common.view.list.CommunityListAdapter
import io.golos.cyber_android.ui.screens.main_activity.communities.tabs.common.view.list.CommunityListScrollListener
import io.golos.cyber_android.ui.screens.main_activity.communities.tabs.common.viewModel.CommunityViewModel
import javax.inject.Inject

abstract class CommunitiesTabFragmentBase<TB: ViewDataBinding> : FragmentBaseMVVM<TB, CommunityModel, CommunityViewModel>(), ChildSearchFragment {
    private lateinit var communitiesListAdapter: CommunityListAdapter
    private lateinit var communitiesListLayoutManager: LinearLayoutManager
    private var communitiesScrollListener: CommunityListScrollListener? = null

    private lateinit var searchListAdapter: CommunityListAdapter
    private lateinit var searchListLayoutManager: LinearLayoutManager

    protected abstract val root: ConstraintLayout
    protected abstract val searchResultList: RecyclerView
    protected abstract val mainList: RecyclerView

    @Inject
    internal lateinit var searchBridge: SearchBridgeChild

    override fun provideViewModelType(): Class<CommunityViewModel> = CommunityViewModel::class.java

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

    @CallSuper
    protected open fun updateSearchResultVisibility(isVisible: Boolean) {
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