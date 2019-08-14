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
import io.golos.cyber_android.ui.base.FragmentBase
import io.golos.cyber_android.ui.common.extensions.reduceDragSensitivity
import io.golos.cyber_android.ui.screens.main_activity.communities.tabs.discover.view.DiscoverFragment
import io.golos.cyber_android.ui.screens.main_activity.communities.tabs.my_community.MyCommunitiesFragment
import io.golos.cyber_android.views.utils.TabLayoutMediator
import io.golos.cyber_android.views.utils.TextWatcherBase
import kotlinx.android.synthetic.main.fragment_communities.*
import kotlinx.android.synthetic.main.view_search_bar.*

class CommunitiesFragment : FragmentBase() {
    enum class Tab(@StringRes val title: Int, val index: Int, val requestCode: Int) {
        DISCOVER(R.string.tab_discover, 0, 100),
        MY_COMMUNITIES(R.string.tab_my_communities, 1, 101)
    }

    companion object {
        fun newInstance() = CommunitiesFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        App.injections.get<CommunitiesFragmentComponent>()
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
                //viewModel.onSearch(s.toString())
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        App.injections.release<CommunitiesFragmentComponent>()
    }

    private fun setupViewPager() {
        communitiesPager.adapter = object : FragmentStateAdapter(childFragmentManager, this.lifecycle) {
            override fun createFragment(position: Int): Fragment {
                return when (position) {
                    Tab.DISCOVER.index -> DiscoverFragment.newInstance()
                    Tab.MY_COMMUNITIES.index -> MyCommunitiesFragment.newInstance()
                    else -> throw RuntimeException("Unsupported tab")
                }
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