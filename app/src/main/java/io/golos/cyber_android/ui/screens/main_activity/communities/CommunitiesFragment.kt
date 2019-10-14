package io.golos.cyber_android.ui.screens.main_activity.communities

import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import io.golos.cyber_android.R
import io.golos.cyber_android.application.App
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.main_activity.communities_fragment.CommunitiesFragmentComponent
import io.golos.cyber_android.ui.common.base.FragmentBase
import io.golos.cyber_android.ui.common.extensions.moveCursorToTheEnd
import io.golos.cyber_android.ui.common.extensions.reduceDragSensitivity
import io.golos.cyber_android.ui.screens.main_activity.communities.search_bridge.ChildSearchFragment
import io.golos.cyber_android.ui.screens.main_activity.communities.search_bridge.ParentSearchFragment
import io.golos.cyber_android.ui.screens.main_activity.communities.search_bridge.SearchBridgeParent
import io.golos.cyber_android.ui.screens.main_activity.communities.tabs.discover.view.DiscoverFragment
import io.golos.cyber_android.ui.screens.main_activity.communities.tabs.my_community.view.MyCommunityFragment
import io.golos.cyber_android.ui.common.utils.TabLayoutMediator
import io.golos.cyber_android.ui.common.utils.TextWatcherBase
import kotlinx.android.synthetic.main.fragment_communities.*
import kotlinx.android.synthetic.main.view_search_bar.*
import javax.inject.Inject

class CommunitiesFragment : FragmentBase(), ParentSearchFragment {
    enum class Tab(@StringRes val title: Int, val index: Int) {
        MY_COMMUNITIES(R.string.tab_my_communities, 0),
        DISCOVER(R.string.tab_discover, 1)
    }

    companion object {
        fun newInstance() = CommunitiesFragment()
    }

    @Inject
    internal lateinit var searchBridge: SearchBridgeParent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        App.injections.get<CommunitiesFragmentComponent>().inject(this)

        searchBridge.attachParent(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_communities, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewPager()
        setupTabLayout()

        searchBar.addTextChangedListener(object : TextWatcherBase() {
            override fun afterTextChanged(s: Editable?) {
                super.afterTextChanged(s)
                searchBridge.getChild(this@CommunitiesFragment.communitiesPager.currentItem).onSearchStringUpdate(s.toString())
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        App.injections.release<CommunitiesFragmentComponent>()
    }

    override fun setSearchString(searchString: String) {
        searchBar.setText(searchString)
        searchBar.moveCursorToTheEnd()
    }

    private fun setupViewPager() {
        communitiesPager.adapter = object : FragmentStateAdapter(childFragmentManager, this.lifecycle) {
            override fun createFragment(position: Int): Fragment {
                val fragment = when (position) {
                    Tab.MY_COMMUNITIES.index -> MyCommunityFragment.newInstance()
                    Tab.DISCOVER.index -> DiscoverFragment.newInstance()
                    else -> throw RuntimeException("Unsupported tab")
                }

                searchBridge.attachChild(position, fragment as ChildSearchFragment)

                return fragment
            }

            override fun getItemCount() = Tab.values().size
        }

        communitiesPager.reduceDragSensitivity()
    }

    private fun setupTabLayout() {
        TabLayoutMediator(tabLayout, communitiesPager) { tab, position ->
            tab.setText(Tab.values()[position].title)
        }.attach()
    }
}