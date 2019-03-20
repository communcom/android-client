package io.golos.cyber_android.ui.screens.feed

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import io.golos.cyber_android.CommunityFeedViewModel
import io.golos.cyber_android.R
import io.golos.cyber_android.serviceLocator
import io.golos.cyber_android.ui.common.posts.PostsAdapter
import io.golos.cyber_android.ui.dialogs.sort.SortingTypeDialogFragment
import io.golos.cyber_android.views.utils.BaseTextWatcher
import io.golos.cyber_android.views.utils.TabLayoutMediator
import io.golos.cyber_android.widgets.sorting.SortingType
import io.golos.cyber_android.widgets.sorting.SortingWidget
import io.golos.cyber_android.widgets.sorting.TimeFilter
import io.golos.cyber_android.widgets.sorting.TrendingSort
import io.golos.domain.interactors.model.CommunityId
import io.golos.domain.interactors.model.PostModel
import kotlinx.android.synthetic.main.fragment_feed.*

const val SORT_REQUEST_CODE = 100

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
        setupViewPager()
        setupSortingWidget()

        TabLayoutMediator(tabLayout, feedPager) { tab, position ->
            tab.setText(FeedPagerAdapter.Tab.values()[position].title)
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

        //todo editorWidget
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SORT_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            onSortSelected(data.getSerializableExtra(SortingTypeDialogFragment.RESULT_TAG) as SortingType)
        }
    }

    private fun onSortSelected(sort: SortingType) {
        when (sort) {
            is TrendingSort -> {
                sortingWidget.setTrendingSort(sort)
                viewModel.onSort(sort)
            }
            is TimeFilter -> {
                sortingWidget.setTimeFilter(sort)
                viewModel.onFilter(sort)
            }
        }
    }

    private fun setupSortingWidget() {
        sortingWidget.listener = object : SortingWidget.Listener {
            override fun onTrendingSortClick() {
                showSortingDialog(arrayOf(TrendingSort.NEW, TrendingSort.TOP))
            }

            override fun onTimeFilterClick() {
                showSortingDialog(
                    arrayOf(
                        TimeFilter.PAST_24_HR,
                        TimeFilter.PAST_WEEK,
                        TimeFilter.PAST_MONTH,
                        TimeFilter.PAST_YEAR,
                        TimeFilter.OF_ALL_TIME
                    )
                )
            }

        }
    }

    private fun showSortingDialog(values: Array<SortingType>) {
        SortingTypeDialogFragment
            .newInstance(values)
            .apply {
                setTargetFragment(this@FeedFragment, SORT_REQUEST_CODE)
            }
            .show(requireFragmentManager(), null)
    }

    private fun setupViewPager() {
        feedPager.adapter = FeedPagerAdapter({ tab ->
            viewModel.pagedListLiveData.observe(this, Observer {
                (feedPager.adapter as FeedPagerAdapter).submitAllList(it)
                feedSwipeRefresh.isRefreshing = false
                Log.i("PostsDataSource s", "submit List 2")
            })
        }, object : PostsAdapter.Listener {
            override fun onPostClick(post: PostModel) {
                Toast.makeText(
                    requireContext(),
                    "post clicked post = ${post.contentId}",
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun onSendClick(post: PostModel, comment: String, upvoted: Boolean, downvoted: Boolean) {
                Toast.makeText(
                    requireContext(),
                    "send comment = ${comment}, upvoted = $upvoted, downvoted = $downvoted",
                    Toast.LENGTH_SHORT
                ).show()
            }

        })
        feedPager.isUserInputEnabled = false
    }

    private fun setupViewModel() {
        viewModel = ViewModelProviders.of(
            this,
            requireActivity().serviceLocator.getCommunityFeedViewModelFactory(CommunityId("gls"))
        ).get(CommunityFeedViewModel::class.java)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        if (savedInstanceState != null) {
            (feedPager.adapter as FeedPagerAdapter).restoreState(savedInstanceState)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        (feedPager.adapter as FeedPagerAdapter).saveState(outState)
    }

    companion object {
        fun newInstance() = FeedFragment()
    }

}
