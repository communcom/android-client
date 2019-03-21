package io.golos.cyber_android.ui.screens.feed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import io.golos.cyber_android.R
import io.golos.cyber_android.serviceLocator
import io.golos.cyber_android.ui.Tags
import io.golos.cyber_android.ui.common.posts.AbstractFeedFragment
import io.golos.cyber_android.ui.common.posts.PostsAdapter
import io.golos.cyber_android.ui.common.posts.PostsDiffCallback
import io.golos.cyber_android.views.utils.TopDividerItemDecoration
import io.golos.domain.entities.CyberUser
import io.golos.domain.interactors.model.PostModel
import io.golos.domain.model.PostFeedUpdateRequest
import kotlinx.android.synthetic.main.fragment_feed_list.*

/**
 * Fragment that represents MY FEED tab of the Feed Page
 */
open class MyFeedFragment :
    AbstractFeedFragment<PostFeedUpdateRequest, FeedPageTabViewModel<PostFeedUpdateRequest>>() {

    override lateinit var viewModel: FeedPageTabViewModel<PostFeedUpdateRequest>

    override val feedList: RecyclerView
        get() = feedRecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_feed_list, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        swipeRefresh.setOnRefreshListener {
            viewModel.requestRefresh()
        }
    }

    override fun onNewData() {
        swipeRefresh.isRefreshing = false
    }

    override fun setupFeedAdapter() {
        feedList.adapter = HeadersPostsAdapter(
            PostsDiffCallback(),
            object : PostsAdapter.Listener {
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
                        "send comment = $comment, upvoted = $upvoted, downvoted = $downvoted",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            },
            isEditorWidgetSupported = true,
            isSortingWidgetSupported = false
        )
        feedList.addItemDecoration(TopDividerItemDecoration(requireContext()))
    }

    override fun setupEventsProvider() {
        (targetFragment as FeedPageLiveDataProvider)
            .provideEventsLiveData().observe(this, Observer {
                when (it) {
                    is FeedPageViewModel.Event.SearchEvent -> viewModel.onSearch(it.query)
                }
            })
    }

    override fun setupWidgetsLiveData() {
        viewModel.editorWidgetStateLiveData.observe(this, Observer {state ->
            (feedList.adapter as HeadersPostsAdapter).apply {
                editorWidgetState = state
            }
        })
    }

    override fun setupViewModel() {
        viewModel = ViewModelProviders.of(
            this,
            requireActivity()
                .serviceLocator
                .getUserSubscriptionsFeedViewModelFactory(CyberUser(arguments?.getString(Tags.USER_ID)!!))
        ).get(UserSubscriptionsFeedViewModel::class.java)
    }

    companion object {
        fun newInstance(userId: String) =
            MyFeedFragment().apply {
                arguments = Bundle().apply {
                    putString(Tags.USER_ID, userId)
                }
            }
    }
}