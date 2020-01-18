package io.golos.cyber_android.ui.screens.community_page_post.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.golos.cyber_android.R
import io.golos.cyber_android.application.App
import io.golos.cyber_android.databinding.FragmentCommunityPostBinding
import io.golos.cyber_android.ui.dto.ContentId
import io.golos.cyber_android.ui.dto.Post
import io.golos.cyber_android.ui.screens.community_page_post.di.CommunityPostFragmentComponent
import io.golos.cyber_android.ui.screens.community_page_post.view_model.CommunityPostViewModel
import io.golos.cyber_android.ui.screens.feed_my.view.list.MyFeedAdapter
import io.golos.cyber_android.ui.screens.post_edit.view.EditorPageActivity
import io.golos.cyber_android.ui.screens.post_edit.view.EditorPageFragment
import io.golos.cyber_android.ui.screens.post_filters.PostFiltersDialog
import io.golos.cyber_android.ui.screens.post_filters.PostFiltersHolder
import io.golos.cyber_android.ui.screens.post_page_menu.model.PostMenu
import io.golos.cyber_android.ui.screens.post_page_menu.view.PostPageMenuDialog
import io.golos.cyber_android.ui.screens.post_report.view.PostReportDialog
import io.golos.cyber_android.ui.screens.post_view.view.PostPageFragment
import io.golos.cyber_android.ui.shared.Tags
import io.golos.cyber_android.ui.shared.mvvm.FragmentBaseMVVM
import io.golos.cyber_android.ui.shared.mvvm.view_commands.NavigateToImageViewCommand
import io.golos.cyber_android.ui.shared.mvvm.view_commands.NavigateToLinkViewCommand
import io.golos.cyber_android.ui.shared.mvvm.view_commands.ViewCommand
import io.golos.cyber_android.ui.shared.paginator.Paginator
import io.golos.cyber_android.ui.shared.utils.DividerPostDecoration
import io.golos.cyber_android.ui.shared.utils.openImageView
import io.golos.cyber_android.ui.shared.utils.openLinkView
import io.golos.cyber_android.ui.shared.utils.shareMessage
import io.golos.cyber_android.ui.shared.widgets.post_comments.items.PostItem
import io.golos.domain.commun_entities.Permlink
import io.golos.domain.use_cases.model.DiscussionIdModel
import kotlinx.android.synthetic.main.fragment_community_post.*
import kotlinx.android.synthetic.main.fragment_community_post.btnRetry
import kotlinx.android.synthetic.main.fragment_community_post.emptyPostProgressLoading
import kotlinx.android.synthetic.main.fragment_my_feed.*
import kotlinx.android.synthetic.main.view_search_bar.*

class CommunityPostFragment : FragmentBaseMVVM<FragmentCommunityPostBinding, CommunityPostViewModel>() {

    override fun provideViewModelType(): Class<CommunityPostViewModel> = CommunityPostViewModel::class.java

    override fun layoutResId(): Int = R.layout.fragment_community_post

    override fun inject(key: String) = App.injections
        .get<CommunityPostFragmentComponent>(
            key,
            arguments!!.getString(COMMUNITY_ID_EXTRA),
            arguments!!.getString(COMMUNITY_ALIAS_EXTRA)
    ).inject(this)

    override fun releaseInjection(key: String) = App.injections.release<CommunityPostFragmentComponent>(key)

    override fun linkViewModel(binding: FragmentCommunityPostBinding, viewModel: CommunityPostViewModel) {
        binding.viewModel = viewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupPostList()
        observeViewModel()

        btnRetry.setOnClickListener {
            viewModel.loadInitialPosts()
        }

        communityFilterContainer.setOnClickListener {
            viewModel.loadFilter()
        }
    }

    override fun processViewCommand(command: ViewCommand) {
        when (command) {
            is NavigationToPostMenuViewCommand -> openPostMenuDialog(command.post)

            is NavigateToPostCommand -> openPost(command.discussionIdModel, command.contentId)

            is SharePostCommand -> requireContext().shareMessage(command.shareUrl)

            is EditPostCommand -> openEditPost(command.post)

            is ReportPostCommand -> openPostReportDialog(command.post)

            is NavigateToImageViewCommand -> requireContext().openImageView(command.imageUri)

            is NavigateToLinkViewCommand -> requireContext().openLinkView(command.link)

            is NavigateToFilterDialogViewCommand -> {
                val timeFilter = command.config.timeFilter
                val periodFilter = command.config.periodFilter

                val tag = CommunityPostFragment::class.java.name
                val postFiltersBottomSheetDialog =
                    PostFiltersDialog.newInstance(
                        isNeedToSaveGlobalFilter = false,
                        timeFilter = timeFilter,
                        periodFilter = periodFilter
                    ).apply {
                        setTargetFragment(this@CommunityPostFragment, PostFiltersDialog.REQUEST)
                    }
                postFiltersBottomSheetDialog.show(requireFragmentManager(), tag)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            PostFiltersDialog.REQUEST -> {
                when (resultCode) {
                    PostFiltersDialog.RESULT_UPDATE_FILTER -> {
                        val filterTime: PostFiltersHolder.UpdateTimeFilter =
                            data?.extras?.getSerializable(Tags.FILTER_TIME) as PostFiltersHolder.UpdateTimeFilter
                        val periodTime: PostFiltersHolder.PeriodTimeFilter =
                            data.extras?.getSerializable(Tags.FILTER_PERIOD_TIME) as PostFiltersHolder.PeriodTimeFilter
                        viewModel.updatePostsByFilter(filterTime, periodTime)
                    }
                }
            }
            PostPageMenuDialog.REQUEST -> {
                when (resultCode) {
                    PostPageMenuDialog.RESULT_ADD_FAVORITE -> {
                        val postMenu: PostMenu? = data?.extras?.getParcelable(Tags.POST_MENU)
                        postMenu?.let {
                            viewModel.addToFavorite(it.permlink)
                        }
                    }
                    PostPageMenuDialog.RESULT_REMOVE_FAVORITE -> {
                        val postMenu: PostMenu? = data?.extras?.getParcelable(Tags.POST_MENU)
                        postMenu?.let {
                            viewModel.removeFromFavorite(it.permlink)
                        }
                    }
                    PostPageMenuDialog.RESULT_SHARE -> {
                        val postMenu: PostMenu? = data?.extras?.getParcelable(Tags.POST_MENU)
                        val shareUrl = postMenu?.shareUrl
                        shareUrl?.let {
                            viewModel.onShareClicked(it)
                        }
                    }
                    PostPageMenuDialog.RESULT_EDIT -> {
                        val postMenu: PostMenu? = data?.extras?.getParcelable(Tags.POST_MENU)
                        postMenu?.let {
                            viewModel.editPost(it.permlink)
                        }
                    }
                    PostPageMenuDialog.RESULT_DELETE -> {
                        val postMenu: PostMenu? = data?.extras?.getParcelable(Tags.POST_MENU)
                        postMenu?.let { menu ->
                            viewModel.deletePost(menu.permlink, menu.communityId)
                        }
                    }
                    PostPageMenuDialog.RESULT_SUBSCRIBE -> {
                        val postMenu: PostMenu? = data?.extras?.getParcelable(Tags.POST_MENU)
                        val communityId = postMenu?.communityId
                        communityId?.let {
                            viewModel.subscribeToCommunity(it)
                        }
                    }
                    PostPageMenuDialog.RESULT_UNSUBSCRIBE -> {
                        val postMenu: PostMenu? = data?.extras?.getParcelable(Tags.POST_MENU)
                        postMenu?.communityId?.let {
                            viewModel.unsubscribeToCommunity(it)
                        }
                    }
                    PostPageMenuDialog.RESULT_REPORT -> {
                        val postMenu: PostMenu? = data?.extras?.getParcelable(Tags.POST_MENU)
                        postMenu?.let {
                            viewModel.onReportPostClicked(it.permlink)
                        }
                    }
                }
            }
            UPDATED_REQUEST_CODE -> {
                when (resultCode) {
                    Activity.RESULT_OK -> {
                        data?.action?.let { action ->
                            when (action) {
                                Tags.ACTION_DELETE -> {
                                    val permlink = data.getStringExtra(Tags.PERMLINK_EXTRA)
                                    if (permlink.isNotEmpty()) {
                                        viewModel.deleteLocalPostByPermlink(permlink)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun setupPostList() {
        val postAdapter = MyFeedAdapter(viewModel, PostItem.Type.FEED)
        postAdapter.click = { item ->
            val postItem = item as? PostItem
            postItem?.let { post ->
                val contentId = post.post.contentId
                val discussionIdModel = DiscussionIdModel(
                    contentId.userId,
                    Permlink(contentId.permlink)
                )
                openPost(discussionIdModel, contentId)
            }
        }

        val lManager = LinearLayoutManager(context)
        with(rvCommunityPosts) {
            addItemDecoration(DividerPostDecoration(requireContext()))
            layoutManager = lManager

            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    val visibleItemCount = recyclerView.childCount
                    val totalItemCount = lManager.itemCount
                    val firstVisibleItem = lManager.findFirstVisibleItemPosition()

                    if (totalItemCount - visibleItemCount <= firstVisibleItem + visibleItemCount) {
                        if (lManager.findLastCompletelyVisibleItemPosition() >= totalItemCount - 1) {
                            viewModel.loadMorePosts()
                        }
                    }
                }
            })

            adapter = postAdapter
        }

        postAdapter.onPageRetryLoadingCallback = {
            viewModel.loadMorePosts()
        }
    }

    private fun observeViewModel() {

        viewModel.filterPostState.observe(viewLifecycleOwner, Observer { filterState ->
            val timeFilterText = filterState.timeFilter.value
            val periodFilterText = filterState.periodFilter.value
            communityFilterType.text = "$timeFilterText, $periodFilterText"
        })

        viewModel.postsListState.observe(viewLifecycleOwner, Observer { state ->
            val postAdapter = rvCommunityPosts.adapter as MyFeedAdapter
            when (state) {
                is Paginator.State.Data<*> -> {
                    postAdapter.hideLoadingNextPageError()
                    postAdapter.hideLoadingNextPageProgress()
                    postAdapter.updateMyFeedPosts(state.data as MutableList<Post>)
                    emptyPostProgressLoading.visibility = View.INVISIBLE
                    communityFilterContainer.visibility = View.VISIBLE
                }
                is Paginator.State.FullData<*> -> {
                    postAdapter.hideLoadingNextPageError()
                    postAdapter.hideLoadingNextPageProgress()
                    emptyPostProgressLoading.visibility = View.INVISIBLE
                    communityFilterContainer.visibility = View.VISIBLE
                }
                is Paginator.State.PageError<*> -> {
                    postAdapter.hideLoadingNextPageProgress()
                    postAdapter.showLoadingNextPageError()
                    rvPosts.scrollToPosition(postAdapter.itemCount - 1)
                    communityFilterContainer.visibility = View.VISIBLE
                }
                is Paginator.State.NewPageProgress<*> -> {
                    postAdapter.hideLoadingNextPageError()
                    postAdapter.showLoadingNextPageProgress()
                    communityFilterContainer.visibility = View.VISIBLE
                }
                is Paginator.State.SearchProgress<*> -> {
                    postAdapter.updateMyFeedPosts(state.data as MutableList<Post>)
                    postAdapter.showLoadingNextPageProgress()
                    pbLoading.visibility = View.VISIBLE
                }
                is Paginator.State.SearchPageError<*> -> {
                    postAdapter.updateMyFeedPosts(state.data as MutableList<Post>)
                    postAdapter.hideLoadingNextPageProgress()
                    uiHelper.showMessage(R.string.loading_error)
                    pbLoading.visibility = View.INVISIBLE
                }
                is Paginator.State.Refresh<*> -> {
                    postAdapter.clearAllPosts()
                    emptyPostProgressLoading.visibility = View.VISIBLE
                    btnRetry.visibility = View.INVISIBLE
                    communityFilterContainer.visibility = View.GONE
                }
                is Paginator.State.EmptyProgress -> {
                    emptyPostProgressLoading.visibility = View.VISIBLE
                    btnRetry.visibility = View.INVISIBLE
                    communityFilterContainer.visibility = View.GONE
                }
                is Paginator.State.Empty -> {
                    emptyPostProgressLoading.visibility = View.INVISIBLE
                    postAdapter.updateMyFeedPosts(mutableListOf())
                    communityFilterContainer.visibility = View.VISIBLE
                }
                is Paginator.State.EmptyError -> {
                    emptyPostProgressLoading.visibility = View.INVISIBLE
                    btnRetry.visibility = View.VISIBLE
                    communityFilterContainer.visibility = View.GONE
                }
            }
        })
    }

    private fun openPost(
        discussionIdModel: DiscussionIdModel,
        contentId: ContentId
    ) {
        getDashboardFragment(this)?.showFragment(
            PostPageFragment.newInstance(
                PostPageFragment.Args(
                    discussionIdModel,
                    contentId
                )
            ),
            tagFragment = contentId.permlink
        )
    }

    private fun openEditPost(post: Post) {
        startActivity(
            EditorPageActivity.getIntent(
                requireContext(),
                EditorPageFragment.Args(
                    contentId = post.contentId
                )
            )
        )
    }

    private fun openPostMenuDialog(postMenu: PostMenu) {
        PostPageMenuDialog.newInstance(postMenu).apply {
            setTargetFragment(this@CommunityPostFragment, PostPageMenuDialog.REQUEST)
        }.show(requireFragmentManager(), "show")
    }

    private fun openPostReportDialog(post: Post) {
        val tag = PostReportDialog::class.java.name
        if (childFragmentManager.findFragmentByTag(tag) == null) {
            val dialog = PostReportDialog.newInstance(PostReportDialog.Args(post.contentId))
            dialog.onPostReportCompleteCallback = {
                viewModel.sendReport(it)
            }
            dialog.show(childFragmentManager, tag)
        }
    }

    companion object {

        private const val COMMUNITY_ID_EXTRA = "community_id"
        private const val COMMUNITY_ALIAS_EXTRA = "community_alias"

        private const val UPDATED_REQUEST_CODE = 41245

        fun newInstance(
            communityId: String,
            alias: String?
        ): CommunityPostFragment = CommunityPostFragment().apply {
            arguments = Bundle().apply {
                putString(COMMUNITY_ID_EXTRA, communityId)
                putString(COMMUNITY_ALIAS_EXTRA, alias)
            }
        }

    }
}