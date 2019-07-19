package io.golos.cyber_android.ui.common.posts

import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import io.golos.cyber_android.serviceLocator
import io.golos.cyber_android.ui.Tags
import io.golos.cyber_android.ui.base.FragmentBase
import io.golos.cyber_android.ui.common.AbstractDiscussionModelAdapter
import io.golos.cyber_android.ui.common.AbstractFeedViewModel
import io.golos.cyber_android.ui.dialogs.ConfirmationDialog
import io.golos.cyber_android.ui.dialogs.NotificationDialog
import io.golos.cyber_android.ui.dialogs.PostPageMenuDialog
import io.golos.cyber_android.ui.screens.editor.EditorPageActivity
import io.golos.cyber_android.ui.screens.editor.EditorPageFragment
import io.golos.cyber_android.utils.PaginationScrollListener
import io.golos.data.errors.AppError
import io.golos.domain.entities.DiscussionEntity
import io.golos.domain.interactors.model.DiscussionIdModel
import io.golos.domain.interactors.model.DiscussionModel
import io.golos.domain.interactors.model.PostModel
import io.golos.domain.requestmodel.FeedUpdateRequest

const val POST_MENU_REQUEST = 301

/**
 * Class represents Fragment that contains some [DiscussionModel] feed (which for now can only be comments or posts).
 * Inheritor should override several fields and methods:
 * 1) [viewModel] and [setupViewModel]. ViewModel should be derived from [AbstractFeedViewModel]. This ViewModel already
 * implements many necessary functions - like voting, discussion creation (for deleting), loading, last page and errors
 * indication etc. @see [AbstractFeedViewModel].
 * 2) [feedList] - this field should return [RecyclerView] on which feed should be displayed.
 * 3) [onNewData] - called when there is some new data in ViewModel appears. This callback method usually is a good place
 * to hide progress bars
 * 4) [setupFeedAdapter] - setup adapter for [feedList]. Adapter should be a [AbstractDiscussionModelAdapter]
 * 5) [setupEventsProvider] - here inheritor should start to parent events
 * 6) [setupWidgetsLiveData] -  allows fragment to setup live data for its widgets (like EditorWidget or SortingWidget)
 */
abstract class AbstractFeedFragment<out R : FeedUpdateRequest,
        E : DiscussionEntity,
        M : DiscussionModel,
        VM : AbstractFeedViewModel<R, E, M>> : FragmentBase() {

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

        @Suppress("UNCHECKED_CAST")
        viewModel.feedLiveData.observe(this, Observer { data ->
            (feedList.adapter as AbstractDiscussionModelAdapter<M>).submit(data)
            onNewData(data)
        })

        viewModel.voteReadinessLiveData.observe(this, Observer { event ->
            event.getIfNotHandled()?.let { ready ->
                if (ready)
                    Toast.makeText(requireContext(), "Ready to vote", Toast.LENGTH_SHORT).show()
            }
        })

        viewModel.getVoteErrorLiveData.observe(this, Observer { event ->
            event.getIfNotHandled()?.let {
                val errorMsg = when (it.second.error) {
                    is AppError.CashoutWindowError -> io.golos.cyber_android.R.string.cashout_window_error
                    is AppError.RequestTimeOutException -> io.golos.cyber_android.R.string.request_timeout_error
                    else -> io.golos.cyber_android.R.string.unknown_error
                }
                NotificationDialog.newInstance(getString(errorMsg))
                    .show(requireFragmentManager(), "error")
            }
        })

        val paginationScrollListener =
            object : PaginationScrollListener(feedListLayoutManager, AbstractPostFeedViewModel.PAGE_SIZE) {
                override fun loadMoreItems() {
                    viewModel.loadMore()
                }
            }
        feedList.addOnScrollListener(paginationScrollListener)

        viewModel.loadingStatusLiveData.observe(this, Observer { isLoading ->
            paginationScrollListener.isLoading = isLoading
        })

        @Suppress("UNCHECKED_CAST")
        viewModel.lastPageLiveData.observe(this, Observer { isLastPage ->
            paginationScrollListener.isLastPage = isLastPage
            (feedList.adapter as AbstractDiscussionModelAdapter<M>).isLoading = !isLastPage
        })
    }

    fun showDiscussionMenu(postModel: PostModel) {
        PostPageMenuDialog.newInstance(
            postModel.isActiveUserDiscussion,
            requireContext().serviceLocator.moshi
                .adapter(DiscussionIdModel::class.java)
                .toJson(postModel.contentId)
        ).apply {
            setTargetFragment(this@AbstractFeedFragment, POST_MENU_REQUEST)
        }.show(requireFragmentManager(), "menu")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == POST_MENU_REQUEST && data != null) {
            when (resultCode) {
                PostPageMenuDialog.RESULT_EDIT -> {
                    val id = requireContext()
                        .serviceLocator
                        .moshi
                        .adapter(DiscussionIdModel::class.java)
                        .fromJson(data.getStringExtra(Tags.DISCUSSION_ID)!!)
                    editDiscussion(id!!)
                }
                PostPageMenuDialog.RESULT_DELETE -> {
                    val id = requireContext()
                        .serviceLocator
                        .moshi
                        .adapter(DiscussionIdModel::class.java)
                        .fromJson(data.getStringExtra(Tags.DISCUSSION_ID)!!)
                    deleteDiscussion(id!!)
                }
            }
        }
    }

    private fun deleteDiscussion(id: DiscussionIdModel) {
        ConfirmationDialog.newInstance(getString(io.golos.cyber_android.R.string.delete_post_confirmation))
            .run {
                listener = { viewModel.deleteDiscussion(id) }
                show(this@AbstractFeedFragment.requireFragmentManager(), "confirmDelete")
            }
    }

    private fun editDiscussion(id: DiscussionIdModel) {
        startActivity(EditorPageActivity.getIntent(requireContext(), EditorPageFragment.Args(id)))
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
    abstract fun onNewData(data: List<M>)

    /**
     * Allows fragment to setup live data for its widgets (like EditorWidget or SortingWidgt)
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

    /**
     * Shows appropriate error of a discussion creation process
     */
    protected fun showDiscussionCreationError(error: Throwable) {
        val errorMsg = when (error) {
            is AppError.CannotDeleteDiscussionWithChildCommentsError -> io.golos.cyber_android.R.string.cant_delete_discussion_with_child_comments
            is AppError.NotEnoughPowerError -> io.golos.cyber_android.R.string.not_enough_power
            is AppError.RequestTimeOutException -> io.golos.cyber_android.R.string.request_timeout_error
            else -> io.golos.cyber_android.R.string.unknown_error
        }

        NotificationDialog.newInstance(getString(errorMsg))
            .show(requireFragmentManager(), "error")
    }
}
