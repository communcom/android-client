package io.golos.cyber_android.ui.screens.main_activity.feed

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import io.golos.commun4j.utils.toCyberName
import io.golos.cyber_android.R
import io.golos.cyber_android.application.App
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.main_activity.trending_feed.TrendingFeedFragmentComponent
import io.golos.cyber_android.ui.Tags
import io.golos.cyber_android.ui.common.mvvm.viewModel.FragmentViewModelFactory
import io.golos.cyber_android.ui.common.posts.AbstractFeedFragment
import io.golos.cyber_android.ui.common.posts.PostsAdapter
import io.golos.cyber_android.ui.dialogs.ImagePickerDialog
import io.golos.cyber_android.ui.dialogs.sort.SortingTypeDialogFragment
import io.golos.cyber_android.ui.screens.main_activity.feed.community.CommunityFeedViewModel
import io.golos.cyber_android.ui.screens.editor_page_activity.EditorPageActivity
import io.golos.cyber_android.ui.shared_fragments.editor.view.EditorPageFragment
import io.golos.cyber_android.ui.shared_fragments.post.view.PostActivity
import io.golos.cyber_android.ui.shared_fragments.post.view.PostPageFragment
import io.golos.cyber_android.ui.screens.profile.ProfileActivity
import io.golos.cyber_android.ui.screens.profile.edit.ImagePickerFragmentBase
import io.golos.cyber_android.ui.common.utils.TopDividerItemDecoration
import io.golos.cyber_android.ui.common.widgets.EditorWidget
import io.golos.cyber_android.ui.common.widgets.sorting.SortingType
import io.golos.cyber_android.ui.common.widgets.sorting.SortingWidget
import io.golos.cyber_android.ui.common.widgets.sorting.TimeFilter
import io.golos.cyber_android.ui.common.widgets.sorting.TrendingSort
import io.golos.domain.commun_entities.CommunityId
import io.golos.domain.entities.PostEntity
import io.golos.domain.interactors.model.PostModel
import io.golos.domain.requestmodel.PostFeedUpdateRequest
import io.golos.domain.requestmodel.QueryResult
import kotlinx.android.synthetic.main.fragment_feed_list.*
import javax.inject.Inject

/**
 * Fragment that represents TRENDING tab of the Feed Page.
 */

class TrendingFeedFragment :
    AbstractFeedFragment<PostFeedUpdateRequest, PostEntity, PostModel, FeedPageTabViewModel<PostFeedUpdateRequest>>() {

    override lateinit var viewModel: FeedPageTabViewModel<PostFeedUpdateRequest>

    @Inject
    internal lateinit var viewModelFactory: FragmentViewModelFactory

    override val feedList: RecyclerView
        get() = feedRecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        App.injections
            .get<TrendingFeedFragmentComponent>(
                CommunityId(arguments?.getString(Tags.COMMUNITY_NAME)!!),
                arguments?.getString(Tags.USER_ID)!!.toCyberName())
            .inject(this)
    }

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

        viewModel.loadingStatusLiveData.observe(this, Observer { status ->
            when(status) {
                is QueryResult.Loading -> swipeRefresh.isRefreshing = true
                else -> swipeRefresh.isRefreshing = false
            }
        })

        viewModel.discussionCreationLiveData.observe(this, Observer {
            it.getIfNotHandled()?.let { result ->
                when (result) {
                    is QueryResult.Loading -> showLoading()
                    is QueryResult.Success -> hideLoading()
                    is QueryResult.Error -> {
                        hideLoading()
                        showDiscussionCreationError(result.error)
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
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(CommunityFeedViewModel::class.java)
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
                    ImagePickerFragmentBase.ImageSource.GALLERY
                ImagePickerDialog.RESULT_CAMERA ->
                    ImagePickerFragmentBase.ImageSource.CAMERA
                ImagePickerDialog.RESULT_DELETE ->
                    ImagePickerFragmentBase.ImageSource.NONE
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