package io.golos.cyber_android.ui.screens.feed

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.paging.PagedList
import io.golos.cyber_android.CommunityFeedViewModel
import io.golos.cyber_android.R
import io.golos.cyber_android.serviceLocator
import io.golos.cyber_android.views.utils.BaseTextWatcher
import io.golos.cyber_android.views.utils.TabLayoutMediator
import io.golos.domain.interactors.model.CommunityId
import io.golos.domain.interactors.model.PostModel
import kotlinx.android.synthetic.main.fragment_feed.*

class FeedFragment : Fragment() {

    private lateinit var viewModel: CommunityFeedViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_feed, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        ViewCompat.setElevation(searchBar, resources.getDimension(R.dimen.elevation_feed_search_bar))

        setupViewModel()

        feedPager.adapter = FeedPagerAdapter(mutableListOf())

        TabLayoutMediator(tabLayout, feedPager) { tab, position ->
            tab.setText(FeedPagerAdapter.Tabs.values()[position].title)
        }.attach()

        searchBar.addTextChangedListener(object : BaseTextWatcher() {
            override fun afterTextChanged(s: Editable?) {
                super.afterTextChanged(s)
                viewModel.onSearch(s.toString())
            }
        })
        feedSwipeRefresh.setOnRefreshListener {
            viewModel.requestRefresh()
        }
        //todo s
        sortingWidget
        editorWidget


    }

    private fun setupViewModel() {
        viewModel = ViewModelProviders.of(
            this,
            requireActivity().serviceLocator.getCommunityFeedViewModelFactory(CommunityId("gls"))
        ).get(CommunityFeedViewModel::class.java)

        viewModel.feedLiveData.observe(this, Observer {
            (feedPager.adapter as FeedPagerAdapter).allFeed.addAll(it)
            (feedPager.adapter as FeedPagerAdapter).notifyDataSetChanged()
            feedSwipeRefresh.isRefreshing = false
        })

//        viewModel.pagedListLiveData.observe(this, Observer<PagedList<PostModel>> {
////            (feedPager.adapter as FeedPagerAdapter).notifyItemChanged(0)
////        })
    }

    companion object {
        fun newInstance() = FeedFragment()
    }

}
