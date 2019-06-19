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
import io.golos.cyber_android.ui.base.LoadingFragment
import io.golos.cyber_android.ui.common.AbstractDiscussionModelAdapter
import io.golos.cyber_android.ui.common.AbstractFeedViewModel
import io.golos.cyber_android.ui.dialogs.ConfirmationDialog
import io.golos.cyber_android.ui.dialogs.PostPageMenuDialog
import io.golos.cyber_android.ui.screens.editor.EditorPageActivity
import io.golos.cyber_android.ui.screens.editor.EditorPageFragment
import io.golos.cyber_android.utils.PaginationScrollListener
import io.golos.domain.entities.DiscussionEntity
import io.golos.domain.interactors.model.DiscussionIdModel
import io.golos.domain.interactors.model.DiscussionModel
import io.golos.domain.interactors.model.PostModel
import io.golos.domain.requestmodel.FeedUpdateRequest

const val POST_MENU_REQUEST = 301

abstract class AbstractFeedFragment<out R : FeedUpdateRequest,
        E : DiscussionEntity,
        M : DiscussionModel,
        VM : AbstractFeedViewModel<R, E, M>> : LoadingFragment() {

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
                Toast.makeText(requireContext(), "Vote Error", Toast.LENGTH_SHORT).show()
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
