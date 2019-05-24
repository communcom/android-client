package io.golos.cyber_android.ui.screens.profile.posts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import io.golos.cyber_android.R
import io.golos.cyber_android.serviceLocator
import io.golos.cyber_android.ui.Tags
import io.golos.cyber_android.ui.common.posts.AbstractFeedFragment
import io.golos.cyber_android.ui.common.posts.PostsAdapter
import io.golos.cyber_android.ui.dialogs.sort.SortingTypeDialogFragment
import io.golos.cyber_android.ui.screens.feed.FeedPageTabViewModel
import io.golos.cyber_android.ui.screens.feed.HeadersPostsAdapter
import io.golos.cyber_android.ui.screens.feed.SORT_REQUEST_CODE
import io.golos.cyber_android.ui.screens.post.PostActivity
import io.golos.cyber_android.ui.screens.post.PostPageFragment
import io.golos.cyber_android.views.utils.TopDividerItemDecoration
import io.golos.cyber_android.widgets.sorting.SortingType
import io.golos.cyber_android.widgets.sorting.SortingWidget
import io.golos.cyber_android.widgets.sorting.TimeFilter
import io.golos.cyber_android.widgets.sorting.TrendingSort
import io.golos.domain.entities.CyberUser
import io.golos.domain.entities.PostEntity
import io.golos.domain.interactors.model.PostModel
import io.golos.domain.requestmodel.PostFeedUpdateRequest
import kotlinx.android.synthetic.main.fragment_user_posts_feed_list.*

/**
 * Fragment that represents POSTS tab of the Profile Page
 */
open class UserPostsFeedFragment :
    AbstractFeedFragment<PostFeedUpdateRequest, PostEntity, PostModel, FeedPageTabViewModel<PostFeedUpdateRequest>>() {

    override lateinit var viewModel: FeedPageTabViewModel<PostFeedUpdateRequest>

    override val feedList: RecyclerView
        get() = feedRecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_user_posts_feed_list, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        swipeRefresh.setOnRefreshListener {
            viewModel.requestRefresh()
        }
        swipeRefresh.isEnabled = false
        setupSortingWidget()
    }

    override fun onNewData(data: List<PostModel>) {
        swipeRefresh.isRefreshing = false
        if (data.isEmpty()) {
            empty.visibility = View.VISIBLE
            feedList.visibility = View.GONE
        } else {
            empty.visibility = View.GONE
            feedList.visibility = View.VISIBLE
        }
    }

    override fun setupFeedAdapter() {
        feedList.adapter = HeadersPostsAdapter(
            object : PostsAdapter.Listener {
                override fun onUpvoteClick(post: PostModel) {
                    viewModel.onUpvote(post)
                }

                override fun onDownvoteClick(post: PostModel) {
                    viewModel.onDownvote(post)
                }

                override fun onPostClick(post: PostModel) {
                    startActivity(PostActivity.getIntent(requireContext(), PostPageFragment.Args(post.contentId)))
                }

                override fun onSendClick(post: PostModel, comment: CharSequence) {
                    viewModel.sendComment(post, comment)
                }

                override fun onPostCommentsClick(post: PostModel) {
                    startActivity(
                        PostActivity.getIntent(
                            requireContext(),
                            PostPageFragment.Args(post.contentId, true)
                        )
                    )
                }
            },
            isEditorWidgetSupported = false,
            isSortingWidgetSupported = true
        )
        feedList.addItemDecoration(TopDividerItemDecoration(requireContext()))
    }

    override fun setupEventsProvider() {
        viewModel.loadingStatusLiveData.observe(this, Observer { isLoading ->
            if (!isLoading)
                swipeRefresh.isRefreshing = false
        })
    }

    override fun setupWidgetsLiveData() {
        viewModel.getSortingWidgetStateLiveData.observe(this, Observer { state ->
            (feedList.adapter as HeadersPostsAdapter).apply {
                sortingWidgetState = state
            }
        })
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
                setTargetFragment(this@UserPostsFeedFragment, SORT_REQUEST_CODE)
            }
            .show(requireFragmentManager(), null)
    }

    override fun setupViewModel() {
        viewModel = ViewModelProviders.of(
            this,
            requireActivity()
                .serviceLocator
                .getUserPostsFeedViewModelFactory(CyberUser(arguments?.getString(Tags.USER_ID)!!))
        ).get(UserPostsFeedViewModel::class.java)
    }

    companion object {
        fun newInstance(userId: String) =
            UserPostsFeedFragment().apply {
                arguments = Bundle().apply {
                    putString(Tags.USER_ID, userId)
                }
            }
    }
}