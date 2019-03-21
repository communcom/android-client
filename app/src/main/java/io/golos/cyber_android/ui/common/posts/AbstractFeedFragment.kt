package io.golos.cyber_android.ui.common.posts

import android.os.Bundle
import android.os.Parcelable
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.golos.cyber_android.ui.screens.feed.HeadersPostsAdapter
import io.golos.domain.model.PostFeedUpdateRequest

abstract class AbstractFeedFragment<out T : PostFeedUpdateRequest, VM : AbstractFeedViewModel<T>> : Fragment() {

    open lateinit var viewModel: VM

    abstract val feedList: RecyclerView

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupViewModel()
        setupEventsProvider()
        setupFeedAdapter()
        feedList.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        viewModel.pagedListLiveData.observe(this, Observer {
            (feedList.adapter as HeadersPostsAdapter).submitList(it)
            onNewData()
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
