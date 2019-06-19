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
import io.golos.cyber_android.ui.dialogs.ImagePickerDialog
import io.golos.cyber_android.ui.dialogs.NotificationDialog
import io.golos.cyber_android.ui.dialogs.sort.SortingTypeDialogFragment
import io.golos.cyber_android.ui.screens.communities.community.CommunityFeedViewModel
import io.golos.cyber_android.ui.screens.editor.EditorPageActivity
import io.golos.cyber_android.ui.screens.editor.EditorPageFragment
import io.golos.cyber_android.ui.screens.post.PostActivity
import io.golos.cyber_android.ui.screens.post.PostPageFragment
import io.golos.cyber_android.ui.screens.profile.ProfileActivity
import io.golos.cyber_android.ui.screens.profile.edit.BaseImagePickerFragment
import io.golos.cyber_android.views.utils.TopDividerItemDecoration
import io.golos.cyber_android.widgets.EditorWidget
import io.golos.cyber_android.widgets.sorting.SortingType
import io.golos.cyber_android.widgets.sorting.SortingWidget
import io.golos.cyber_android.widgets.sorting.TimeFilter
import io.golos.cyber_android.widgets.sorting.TrendingSort
import io.golos.data.errors.CannotDeleteDiscussionWithChildCommentsException
import io.golos.domain.entities.PostEntity
import io.golos.domain.interactors.model.CommunityId
import io.golos.domain.interactors.model.PostModel
import io.golos.domain.requestmodel.PostFeedUpdateRequest
import io.golos.domain.requestmodel.QueryResult
import kotlinx.android.synthetic.main.fragment_feed_list.*

/**
 * Fragment that represents TRENDING tab of the Feed Page.
 */

class TrendingFeedFragment :
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
        setupSortingWidget()
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

                override fun onAuthorClick(post: PostModel) {
                    startActivity(ProfileActivity.getIntent(requireContext(), post.author.userId.userId))
                }

                override fun onPostMenuClick(postModel: PostModel) {
                    showDiscussionMenu(postModel)
                }
            },
            isEditorWidgetSupported = true,
            isSortingWidgetSupported = true
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
                    is QueryResult.Loading -> showLoading()
                    is QueryResult.Success -> hideLoading()
                    is QueryResult.Error -> {
                        hideLoading()
                        //Toast.makeText(requireContext(), "${result.error::class} e3 = ${result.error.message}", Toast.LENGTH_SHORT).show()
                        when (result.error) {
                            is CannotDeleteDiscussionWithChildCommentsException ->
                                NotificationDialog.newInstance(getString(R.string.cant_delete_discussion_with_child_comments))
                                    .show(requireFragmentManager(), "delete error")

                            else -> Toast.makeText(requireContext(), "Post creation failed", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        })
    }

    override fun setupWidgetsLiveData() {
        viewModel.getSortingWidgetStateLiveData.observe(this, Observer { state ->
            (feedList.adapter as HeadersPostsAdapter).apply {
                sortingWidgetState = state
            }
        })

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
                .getCommunityFeedViewModelFactory(
                    CommunityId(arguments?.getString(Tags.COMMUNITY_NAME)!!),
                    arguments?.getString(Tags.USER_ID)!!.toCyberName()
                )
        ).get(CommunityFeedViewModel::class.java)
    }

    private fun setupEditorWidget() {
        (feedList.adapter as HeadersPostsAdapter).editorWidgetListener = object : EditorWidget.Listener {
            override fun onGalleryClick() {
                ImagePickerDialog.newInstance(ImagePickerDialog.Target.EDITOR_PAGE).apply {
                    setTargetFragment(this@TrendingFeedFragment, EDITOR_WIDGET_PHOTO_REQUEST_CODE)
                }.show(requireFragmentManager(), "cover")
            }

            override fun onWidgetClick() {
                startActivityForResult(
                    EditorPageActivity.getIntent(
                        requireContext()
                    ), REQUEST_POST_CREATION
                )
            }
        }
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
                setTargetFragment(this@TrendingFeedFragment, SORT_REQUEST_CODE)
            }
            .show(requireFragmentManager(), null)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SORT_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            onSortSelected(data.getSerializableExtra(SortingTypeDialogFragment.RESULT_TAG) as SortingType)
        }
        if (requestCode == REQUEST_POST_CREATION && resultCode == Activity.RESULT_OK) {
            viewModel.requestRefresh()
        }

        if (requestCode == EDITOR_WIDGET_PHOTO_REQUEST_CODE) {
            val target = when (resultCode) {
                ImagePickerDialog.RESULT_GALLERY ->
                    BaseImagePickerFragment.ImageSource.GALLERY
                ImagePickerDialog.RESULT_CAMERA ->
                    BaseImagePickerFragment.ImageSource.CAMERA
                ImagePickerDialog.RESULT_DELETE ->
                    BaseImagePickerFragment.ImageSource.NONE
                else -> null
            }
            if (target != null) startActivityForResult(
                EditorPageActivity.getIntent(
                    requireContext(),
                    EditorPageFragment.Args(initialImageSource = target)
                ), REQUEST_POST_CREATION
            )
        }
    }

    private fun onSortSelected(sort: SortingType) {
        when (sort) {
            is TrendingSort -> {
                viewModel.onSort(sort)
            }
            is TimeFilter -> {
                viewModel.onFilter(sort)
            }
        }
    }

    companion object {
        fun newInstance(communityName: String, userId: String) =
            TrendingFeedFragment().apply {
                arguments = Bundle().apply {
                    putString(Tags.COMMUNITY_NAME, communityName)
                    putString(Tags.USER_ID, userId)
                }
            }
    }
}