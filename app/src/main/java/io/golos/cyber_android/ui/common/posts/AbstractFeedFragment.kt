package io.golos.cyber_android.ui.common.posts

import android.os.Bundle
import android.os.Parcelable
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import io.golos.cyber_android.ui.screens.feed.HeadersPostsAdapter
import io.golos.cyber_android.utils.PaginationScrollListener
import io.golos.domain.model.PostFeedUpdateRequest

abstract class AbstractFeedFragment<out T : PostFeedUpdateRequest, VM : AbstractFeedViewModel<T>> : Fragment() {

    open lateinit var viewModel: VM

    abstract val feedList: RecyclerView

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupViewModel()
        setupEventsProvider()
        setupFeedAdapter()
        setupWidgetsLiveData()
        val feedListLayoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        feedList.layoutManager = feedListLayoutManager
        (feedList.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        viewModel.feedLiveData.observe(this, Observer {
            (feedList.adapter as HeadersPostsAdapter).submit(it)
            onNewData()
        })

        viewModel.voteReadinessLiveData.observe(this, Observer { event ->
            event.getIfNotHandled()?.let { ready ->
                if (ready)
                    Toast.makeText(requireContext(), "Ready to vote", Toast.LENGTH_SHORT).show()
            }
        })

        viewModel.voteErrorLiveData.observe(this, Observer { event ->
            event.getIfNotHandled()?.let {
                Toast.makeText(requireContext(), "Vote Error", Toast.LENGTH_SHORT).show()
            }
        })

        val paginationScrollListener = object : PaginationScrollListener(feedListLayoutManager, AbstractFeedViewModel.PAGE_SIZE) {
            override fun loadMoreItems() {
                viewModel.loadMore()
            }
        }
        feedList.addOnScrollListener(paginationScrollListener)

        viewModel.loadingStatusLiveData.observe(this, Observer { isLoading ->
            paginationScrollListener.isLoading = isLoading
        })

        viewModel.lastPageLiveData.observe(this, Observer { isLastPage ->
            paginationScrollListener.isLastPage = isLastPage
            (feedList.adapter as HeadersPostsAdapter).isLoading = !isLastPage
        })


    }

    /**
     * Allows fragment to setup event listener from parent fragments
     */
    abstract fun setupEventsProvider()

    /**
     * Allows fragment to setup feed adapter to [feedList]
     */
    abstract fun setupFeedAdapter()

    /**
     * Allows fragment to setup view model into [viewModel]
     */
    abstract fun setupViewModel()

    /**
     * Called when adapter of [feedList] receives some new data
     */
    abstract fun onNewData()

    /**
     * Called when adapter of [feedList] receives some new data
     */
    abstract fun setupWidgetsLiveData()

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        if (savedInstanceState != null) {
            val savedRecyclerLayoutState = savedInstanceState.getParcelable(javaClass.name) as Parcelable
            feedList.layoutManager?.onRestoreInstanceState(savedRecyclerLayoutState)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (feedList.layoutManager != null)
            outState.putParcelable(javaClass.name, feedList.layoutManager?.onSaveInstanceState())
    }
}
