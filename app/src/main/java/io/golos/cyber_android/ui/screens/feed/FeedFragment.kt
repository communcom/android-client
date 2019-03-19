package io.golos.cyber_android.ui.screens.feed

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat

import io.golos.cyber_android.R
import io.golos.cyber_android.views.utils.TabLayoutMediator
import kotlinx.android.synthetic.main.fragment_feed.*

class FeedFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_feed, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        ViewCompat.setElevation(searchBar, resources.getDimension(R.dimen.elevation_feed_search_bar))

        feedPager.adapter = FeedPagerAdapter()

        TabLayoutMediator(tabLayout, feedPager) { tab, position ->
            tab.setText(FeedPagerAdapter.Tabs.values()[position].title)
        }.attach()
    }

    companion object {
        fun newInstance() = FeedFragment()
    }

}
