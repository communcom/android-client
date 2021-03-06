package io.golos.cyber_android.ui.screens.community_page_post.view

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.annotation.StringRes
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.golos.cyber_android.R
import io.golos.cyber_android.application.App
import io.golos.cyber_android.databinding.FragmentCommunityPostBinding
import io.golos.cyber_android.ui.dialogs.PostRewardBottomSheetDialog
import io.golos.cyber_android.ui.dialogs.SelectRewardCurrencyDialog
import io.golos.cyber_android.ui.dialogs.donation.DonationUsersDialog
import io.golos.cyber_android.ui.dto.Post
import io.golos.cyber_android.ui.screens.community_page.child_pages.community_post.model.TimeConfigurationDomain
import io.golos.cyber_android.ui.screens.community_page_post.di.CommunityPostFragmentComponent
import io.golos.cyber_android.ui.screens.community_page_post.view_model.CommunityPostViewModel
import io.golos.cyber_android.ui.screens.donate_send_points.view.DonateSendPointsFragment
import io.golos.cyber_android.ui.screens.feed_my.view.list.MyFeedAdapter
import io.golos.cyber_android.ui.screens.post_edit.activity.EditorPageActivity
import io.golos.cyber_android.ui.screens.post_edit.fragment.view.EditorPageFragment
import io.golos.cyber_android.ui.screens.post_filters.PostFiltersDialog
import io.golos.cyber_android.ui.screens.post_filters.PostFiltersHolder
import io.golos.cyber_android.ui.screens.post_page_menu.model.PostMenu
import io.golos.cyber_android.ui.screens.post_page_menu.view.PostPageMenuDialog
import io.golos.cyber_android.ui.screens.post_report.view.PostReportDialog
import io.golos.cyber_android.ui.screens.post_view.view.PostPageFragment
import io.golos.cyber_android.ui.screens.profile.view.ProfileExternalUserFragment
import io.golos.cyber_android.ui.shared.Tags
import io.golos.cyber_android.ui.shared.mvvm.FragmentBaseMVVM
import io.golos.cyber_android.ui.shared.mvvm.view_commands.*
import io.golos.cyber_android.ui.shared.paginator.Paginator
import io.golos.cyber_android.ui.shared.utils.DividerPostDecoration
import io.golos.cyber_android.ui.shared.utils.openImageView
import io.golos.cyber_android.ui.shared.utils.openLinkView
import io.golos.cyber_android.ui.shared.utils.shareMessage
import io.golos.cyber_android.ui.shared.widgets.post_comments.items.PostItem
import io.golos.domain.commun_entities.Permlink
import io.golos.domain.dto.CommunityIdDomain
import io.golos.domain.dto.ContentIdDomain
import io.golos.domain.dto.RewardCurrency
import io.golos.domain.dto.UserIdDomain
import io.golos.domain.use_cases.model.DiscussionIdModel
import kotlinx.android.synthetic.main.fragment_community_post.*
import kotlinx.android.synthetic.main.fragment_community_post.btnRetry
import kotlinx.android.synthetic.main.fragment_community_post.emptyPostProgressLoading
import kotlinx.android.synthetic.main.fragment_my_feed.*
import kotlinx.android.synthetic.main.view_search_bar.*

class CommunityPostFragment : FragmentBaseMVVM<FragmentCommunityPostBinding, CommunityPostViewModel>() {
    companion object {
        private const val COMMUNITY_ID_EXTRA = "community_id"

        private const val REQUEST_FOR_RESULT_FROM_EDIT = 41522

        fun newInstance(communityId: CommunityIdDomain): CommunityPostFragment = CommunityPostFragment().apply {
            arguments = Bundle().apply {
                putParcelable(COMMUNITY_ID_EXTRA, communityId)
            }
        }
    }

    override fun provideViewModelType(): Class<CommunityPostViewModel> = CommunityPostViewModel::class.java

    override fun layoutResId(): Int = R.layout.fragment_community_post

    override fun inject(key: String) =
        App.injections.get<CommunityPostFragmentComponent>(key, arguments!!.getParcelable<CommunityIdDomain>(COMMUNITY_ID_EXTRA)).inject(this)

    override fun releaseInjection(key: String) = App.injections.release<CommunityPostFragmentComponent>(key)

    override fun linkViewModel(binding: FragmentCommunityPostBinding, viewModel: CommunityPostViewModel) {
        binding.viewModel = viewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupPostList(viewModel.rewardCurrency.value!!)
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
            is SharePostCommand -> requireContext().shareMessage(command.shareUrl, command.currentUserId)
            is EditPostCommand -> openEditPost(command.post)
            is ReportPostCommand -> openPostReportDialog(command.post)
            is NavigateToImageViewCommand -> requireContext().openImageView(command.imageUri)
            is NavigateToLinkViewCommand -> requireContext().openLinkView(command.link)
            is NavigateToFilterDialogViewCommand -> openFilterDialog(command.config)
            is ViewInExplorerViewCommand -> viewInExplorer(command.exploreUrl)
            is ShowPostRewardDialogCommand -> showPostRewardDialog(command.titleResId, command.textResId)
            is NavigateToDonateCommand -> moveToDonate(command)
            is ShowDonationUsersDialogCommand -> showDonationUsersDialogCommand(command.post)
            is NavigateToUserProfileCommand -> openUserProfile(command.userId)
            is SelectRewardCurrencyDialogCommand -> showSelectRewardCurrencyDialog(command.startCurrency)
        }
    }

    private fun viewInExplorer(exploreUrl: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(exploreUrl))
        startActivity(intent)
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
            PostPageFragment.UPDATED_REQUEST_CODE -> {
                val permlink = data?.getStringExtra(Tags.PERMLINK_EXTRA)
                if (!permlink.isNullOrBlank()) {
                    viewModel.deleteLocalPostByPermlink(permlink)
                }
            }

            REQUEST_FOR_RESULT_FROM_EDIT -> {
                when (resultCode) {
                    Activity.RESULT_OK -> {
                        data?.action?.let { action ->
                            when (action) {
                                Tags.ACTION_EDIT_SUCCESS -> {
                                    val contentId = data.getParcelableExtra<ContentIdDomain>(Tags.CONTENT_ID)
                                    val discussionIdModel =
                                        DiscussionIdModel(contentId.userId.userId, Permlink(contentId.permlink))
                                    openPost(discussionIdModel, contentId)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun setupPostList(rewardCurrency: RewardCurrency) {
        val postAdapter = MyFeedAdapter(viewModel, PostItem.Type.FEED, viewModel.recordPostViewManager, rewardCurrency)
        postAdapter.click = { item ->
            val postItem = item as? PostItem
            postItem?.let { post ->
                val contentId = post.post.contentId
                val discussionIdModel = DiscussionIdModel(contentId.userId.userId, Permlink(contentId.permlink))
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
            val timeFilterText = getString(filterState.timeFilter.value)
            val periodFilterText = getString(filterState.periodFilter.value)
            communityFilterType.text =
                "$timeFilterText ${if (filterState.timeFilter.value == PostFiltersHolder.UpdateTimeFilter.POPULAR.value) ", $periodFilterText" else ""}"
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

        viewModel.rewardCurrency.observe(viewLifecycleOwner, Observer {
            (rvCommunityPosts.adapter as MyFeedAdapter).updateRewardCurrency(it)
        })
    }

    private fun openPost(discussionIdModel: DiscussionIdModel, contentId: ContentIdDomain) {
        getDashboardFragment(this)?.navigateToFragment(PostPageFragment.newInstance(PostPageFragment.Args(discussionIdModel, contentId)) {
            viewModel.updatePostItem(it)
        }, tag = contentId.permlink)
    }

    private fun openEditPost(post: Post) {
        startActivityForResult(EditorPageActivity.getIntent(requireContext(), EditorPageFragment.Args(contentId = post.contentId)), REQUEST_FOR_RESULT_FROM_EDIT)
    }

    private fun openPostMenuDialog(postMenu: PostMenu) {
        PostPageMenuDialog.show(this, viewModel.isPostSubscriptionModified.value!!, postMenu) {
            when (it) {
                is PostPageMenuDialog.Result.ViewInExplorer -> viewModel.viewInExplorer(it.postMenu)
                is PostPageMenuDialog.Result.AddFavorite -> viewModel.addToFavorite(it.postMenu.permlink)
                is PostPageMenuDialog.Result.RemoveFavorite -> viewModel.removeFromFavorite(it.postMenu.permlink)
                is PostPageMenuDialog.Result.Share -> it.postMenu.shareUrl?.let { viewModel.onShareClicked(it) }
                is PostPageMenuDialog.Result.Edit -> viewModel.editPost(it.postMenu.permlink)
                is PostPageMenuDialog.Result.Delete -> viewModel.deletePost(it.postMenu.permlink, it.postMenu.communityId)
                is PostPageMenuDialog.Result.Subscribe -> viewModel.subscribeToCommunity(it.postMenu.communityId)
                is PostPageMenuDialog.Result.Unsubscribe -> viewModel.unsubscribeToCommunity(it.postMenu.communityId)
                is PostPageMenuDialog.Result.Report -> viewModel.onReportPostClicked(it.postMenu.permlink)
            }
        }
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

    private fun showPostRewardDialog(@StringRes titleResId: Int, @StringRes textResId: Int) =
        PostRewardBottomSheetDialog.show(this, titleResId, textResId) { }

    private fun openFilterDialog(timeConfig: TimeConfigurationDomain) {
        val tag = CommunityPostFragment::class.java.name
        val postFiltersBottomSheetDialog =
            PostFiltersDialog.newInstance(isNeedToSaveGlobalFilter = false, timeFilter = timeConfig.timeFilter, periodFilter = timeConfig.periodFilter).apply {
                setTargetFragment(this@CommunityPostFragment, PostFiltersDialog.REQUEST)
            }
        postFiltersBottomSheetDialog.show(requireFragmentManager(), tag)
    }

    private fun moveToDonate(command: NavigateToDonateCommand) =
        getDashboardFragment(this)?.navigateToFragment(DonateSendPointsFragment.newInstance(command.contentId, command.communityId, command.contentAuthor, command.balance, command.amount))

    private fun showDonationUsersDialogCommand(post: Post) =

        DonationUsersDialog.show(this, post, closeAction = {
            (it as? DonationUsersDialog.Result.ItemSelected)?.user?.let { viewModel.onUserClicked(it.userId) }
        }, onUserClickListener = {
            viewModel.onUserClicked(it)
        }, onDonateClickListener = { donateType, contentId, communityId, author ->
            viewModel.onDonateClick(donateType, contentId, communityId, author)
        })

    private fun openUserProfile(userId: UserIdDomain) {
        getDashboardFragment(this)?.navigateToFragment(ProfileExternalUserFragment.newInstance(userId))
    }

    private fun showSelectRewardCurrencyDialog(currency: RewardCurrency) = SelectRewardCurrencyDialog.show(this, currency) {
        it?.rewardCurrency?.let { currency -> viewModel.updateRewardCurrency(currency) }
    }
}