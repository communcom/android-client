package io.golos.cyber_android.ui.screens.feed

import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import androidx.viewpager2.adapter.FragmentStateAdapter
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.Tags
import io.golos.cyber_android.views.utils.BaseTextWatcher
import io.golos.cyber_android.views.utils.TabLayoutMediator
import kotlinx.android.synthetic.main.fragment_feed.*


const val SORT_REQUEST_CODE = 100
const val FEED_REQUEST_CODE = 101

class FeedFragment : Fragment(), FeedPageLiveDataProvider {

    enum class Tab(@StringRes val title: Int, val index: Int) {
        ALL(R.string.tab_all, 0), MY_FEED(R.string.tab_my_feed, 1)
    }

    private lateinit var viewModel: FeedPageViewModel

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
        setupViewPager()
        setupTabLayout()

        searchBar.addTextChangedListener(object : BaseTextWatcher() {
            override fun afterTextChanged(s: Editable?) {
                super.afterTextChanged(s)
                viewModel.onSearch(s.toString())
            }
        })

        val searchIcon = VectorDrawableCompat.create(resources, R.drawable.ic_search, null)
        searchBar.setCompoundDrawablesWithIntrinsicBounds(searchIcon, null, null, null)
    }

    private fun setupViewPager() {
        feedPager.adapter = object : FragmentStateAdapter(requireFragmentManager()) {
            override fun getItem(position: Int): Fragment {
                return when (position) {
                    Tab.ALL.index -> AllFeedFragment.newInstance(arguments?.getString(Tags.COMMUNITY_NAME)!!).apply {
                        setTargetFragment(this@FeedFragment, FEED_REQUEST_CODE)
                    }
                    Tab.MY_FEED.index -> MyFeedFragment.newInstance(arguments?.getString(Tags.USER_ID)!!).apply {
                        setTargetFragment(this@FeedFragment, FEED_REQUEST_CODE)
                    }
                    else -> throw RuntimeException("Unsupported tab")
                }
            }

            override fun getItemCount() = Tab.values().size

        }
    }

    private fun setupViewModel() {
        viewModel = ViewModelProviders.of(this).get(FeedPageViewModel::class.java)
    }

    private fun setupTabLayout() {
        TabLayoutMediator(tabLayout, feedPager) { tab, position ->
            tab.setText(Tab.values()[position].title)
        }.attach()
    }

    override fun provideEventsLiveData() = viewModel.getEventsLiveData

    companion object {
        fun newInstance(communityName: String, userId: String) =
            FeedFragment().apply {
                arguments = Bundle().apply {
                    putString(Tags.COMMUNITY_NAME, communityName)
                    putString(Tags.USER_ID, userId)
                }
            }
    }
}
