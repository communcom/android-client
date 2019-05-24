package io.golos.cyber_android.ui.screens.feed

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import io.golos.cyber4j.utils.toCyberName
import io.golos.cyber_android.R
import io.golos.cyber_android.serviceLocator
import io.golos.cyber_android.ui.Tags
import io.golos.cyber_android.ui.common.posts.AbstractFeedFragment
import io.golos.cyber_android.ui.common.posts.PostsAdapter
import io.golos.cyber_android.ui.screens.editor.EditorPageActivity
import io.golos.cyber_android.ui.screens.editor.EditorPageFragment
import io.golos.cyber_android.ui.screens.editor.EditorPageViewModel
import io.golos.cyber_android.ui.screens.post.PostActivity
import io.golos.cyber_android.ui.screens.post.PostPageFragment
import io.golos.cyber_android.views.utils.TopDividerItemDecoration
import io.golos.cyber_android.widgets.EditorWidget
import io.golos.domain.entities.CyberUser
import io.golos.domain.entities.PostEntity
import io.golos.domain.interactors.model.PostModel
import io.golos.domain.requestmodel.PostFeedUpdateRequest
import io.golos.domain.requestmodel.QueryResult
import kotlinx.android.synthetic.main.fragment_feed_list.*

/**
 * Fragment that represents MY FEED tab of the Feed Page
 */

open class MyFeedFragment :
    AbstractFeedFragment<PostFeedUpdateRequest, PostEntity, PostModel, FeedPageTabViewModel<PostFeedUpdateRequest>>() {

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
        setupEditorWidget()
    }

    override fun onNewData(data: List<PostModel>) {
        swipeRefresh.isRefreshing = false
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

                override fun onPostShare(post: PostModel) {

                }
            },
            isEditorWidgetSupported = true,
            isSortingWidgetSupported = false
        )
        feedList.addItemDecoration(TopDividerItemDecoration(requireContext()))
    }

    override fun setupEventsProvider() {
        (targetFragment as? FeedPageLiveDataProvider)
            ?.provideEventsLiveData()?.observe(this, Observer {
                when (it) {
                    is FeedPageViewModel.Event.SearchEvent -> viewModel.onSearch(it.query)
                }
            })

        viewModel.loadingStatusLiveData.observe(this, Observer { isLoading ->
            if (!isLoading)
                swipeRefresh.isRefreshing = false
        })

        viewModel.discussionCreationLiveData.observe(this, Observer {
            it.getIfNotHandled()?.let { result ->
                when (result) {
                    is QueryResult.Loading<*> -> showLoading()
                    is QueryResult.Success<*> -> hideLoading()
                    is QueryResult.Error<*> -> {
                        hideLoading()
                        Toast.makeText(requireContext(), "Post creation failed", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }

    private fun setupEditorWidget() {
        (feedList.adapter as HeadersPostsAdapter).editorWidgetListener = object : EditorWidget.Listener {
            override fun onGalleryClick() {
            }

            override fun onWidgetClick() {
                startActivityForResult(
                    EditorPageActivity.getIntent(
                        requireContext(),
                        EditorPageFragment.Args(EditorPageViewModel.Type.POST)
                    ), REQUEST_POST_CREATION
                )
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_POST_CREATION && resultCode == Activity.RESULT_OK) {
            viewModel.requestRefresh()
        }
    }

    override fun setupWidgetsLiveData() {
        viewModel.getEditorWidgetStateLiveData.observe(this, Observer { state ->
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
                .getUserSubscriptionsFeedViewModelFactory(
                    CyberUser(arguments?.getString(Tags.USER_ID)!!),
                    arguments?.getString(Tags.USER_ID)!!.toCyberName())
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