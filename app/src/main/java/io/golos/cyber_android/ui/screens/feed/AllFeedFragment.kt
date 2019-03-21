package io.golos.cyber_android.ui.screens.feed

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import io.golos.cyber_android.ui.dialogs.sort.SortingTypeDialogFragment
import io.golos.cyber_android.widgets.sorting.SortingType
import io.golos.cyber_android.widgets.sorting.SortingWidget
import io.golos.cyber_android.widgets.sorting.TimeFilter
import io.golos.cyber_android.widgets.sorting.TrendingSort

/**
 * Fragment that represents ALL tab of the Feed Page. Derived from [MyFeedFragment] since the only difference
 * between them is [SortingWidget] in [HeadersPostsAdapter]
 */
class AllFeedFragment : MyFeedFragment() {

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupSortingWidget()
    }

    override fun setupFeedAdapter() {
        super.setupFeedAdapter()
        (feedList.adapter as HeadersPostsAdapter).supportSortingWidget = true
    }

    private fun setupSortingWidget() {
        (feedList.adapter as HeadersPostsAdapter).sortingWidgetListener = object : SortingWidget.Listener {
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
                setTargetFragment(this@AllFeedFragment, SORT_REQUEST_CODE)
            }
            .show(requireFragmentManager(), null)
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
                (feedList.adapter as HeadersPostsAdapter).setTrendingSort(sort)
                viewModel.onSort(sort)
            }
            is TimeFilter -> {
                (feedList.adapter as HeadersPostsAdapter).setTimeFilter(sort)
                viewModel.onFilter(sort)
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable("sortState", (feedList.adapter as HeadersPostsAdapter).sortingWidgetState)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        val sortState = savedInstanceState?.getParcelable("sortState") as HeadersPostsAdapter.SortingWidgetState?
        (feedList.adapter as HeadersPostsAdapter).apply {
            sortState?.let {
                setTrendingSort(sortState.sort)
                setTimeFilter(sortState.filter)
            }
        }
    }

    companion object {
        fun newInstance() = AllFeedFragment()
    }
}