package io.golos.cyber_android.ui.screens.profile_posts.view

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
import io.golos.cyber_android.databinding.FragmentProfilePostsBinding
import io.golos.cyber_android.ui.dialogs.PostRewardBottomSheetDialog
import io.golos.cyber_android.ui.dialogs.SelectRewardCurrencyDialog
import io.golos.cyber_android.ui.dialogs.donation.DonationUsersDialog
import io.golos.cyber_android.ui.dto.Post
import io.golos.cyber_android.ui.screens.community_page.view.CommunityPageFragment
import io.golos.cyber_android.ui.screens.dashboard.view.DashboardFragment
import io.golos.cyber_android.ui.screens.donate_send_points.view.DonateSendPointsFragment
import io.golos.cyber_android.ui.screens.feed_my.view.list.MyFeedAdapter
import io.golos.cyber_android.ui.screens.post_edit.activity.EditorPageActivity
import io.golos.cyber_android.ui.screens.post_edit.fragment.view.EditorPageFragment
import io.golos.cyber_android.ui.screens.post_page_menu.model.PostMenu
import io.golos.cyber_android.ui.screens.post_page_menu.view.PostPageMenuDialog
import io.golos.cyber_android.ui.screens.post_report.view.PostReportDialog
import io.golos.cyber_android.ui.screens.post_view.view.PostPageFragment
import io.golos.cyber_android.ui.screens.profile.view.ProfileExternalUserFragment
import io.golos.cyber_android.ui.screens.profile.view.ProfileFragment
import io.golos.cyber_android.ui.screens.profile_posts.di.ProfilePostsFragmentComponent
import io.golos.cyber_android.ui.screens.profile_posts.view_commands.*
import io.golos.cyber_android.ui.screens.profile_posts.view_model.ProfilePostsViewModel
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
import io.golos.domain.dto.*
import io.golos.domain.use_cases.model.DiscussionIdModel
import kotlinx.android.synthetic.main.fragment_profile_posts.*
import kotlinx.android.synthetic.main.view_search_bar.*

open class ProfilePostsFragment : FragmentBaseMVVM<FragmentProfilePostsBinding, ProfilePostsViewModel>() {
    companion object {

        private const val REQUEST_FOR_RESULT_FROM_EDIT = 41223

        fun newInstance() = ProfilePostsFragment()
    }

    override fun linkViewModel(binding: FragmentProfilePostsBinding, viewModel: ProfilePostsViewModel) {
        binding.viewModel = viewModel
    }

    override fun provideViewModelType(): Class<ProfilePostsViewModel> = ProfilePostsViewModel::class.java

    override fun layoutResId(): Int = R.layout.fragment_profile_posts

    override fun inject(key: String) {
        App.injections
            .get<ProfilePostsFragmentComponent>(key, PostsConfigurationDomain.TypeFeedDomain.NEW)
            .inject(this)
    }

    override fun releaseInjection(key: String) = App.injections.release<ProfilePostsFragmentComponent>(key)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupPostsList(viewModel.rewardCurrency.value!!)
        observeViewModel()
        viewModel.start()
    }

    override fun processViewCommand(command: ViewCommand) {
        when (command) {
            is NavigateToImageViewCommand -> requireContext().openImageView(command.imageUri)
            is NavigateToLinkViewCommand -> requireContext().openLinkView(command.link)
            is NavigateToUserProfileCommand -> openUserProfile(command.userId)
            is ScrollProfileToTopCommand -> scrollProfileToTop()
            is NavigateToCommunityPageCommand -> openCommunityPage(command.communityId)
            is NavigateToPostCommand -> openPost(command.discussionIdModel, command.contentId)
            is NavigationToPostMenuViewCommand -> openPostMenuDialog(command.post)
            is SharePostCommand -> sharePost(command.shareUrl)
            is EditPostCommand -> editPost(command.post)
            is ViewInExplorerViewCommand -> viewInExplorer(command.exploreUrl)
            is ReportPostCommand -> openPostReport(command.post)
            is ShowPostRewardDialogCommand -> showPostRewardDialog(command.titleResId, command.textResId)
            is NavigateToDonateCommand -> moveToDonate(command)
            is ShowDonationUsersDialogCommand -> showDonationUsersDialogCommand(command.donation)
            is SelectRewardCurrencyDialogCommand -> showSelectRewardCurrencyDialog(command.startCurrency)
        }
    }

    private fun scrollProfileToTop() {
        when(parentFragment) {
            is DashboardFragment -> (parentFragment as DashboardFragment).scrollProfileToTop()
            is ProfileExternalUserFragment -> (parentFragment as ProfileFragment).scrollToTop()
        }
    }

    private fun openUserProfile(userId: UserIdDomain){
        getDashboardFragment(this)?.navigateToFragment(
            ProfileExternalUserFragment.newInstance(userId)
        )
    }

    private fun openCommunityPage(communityId: CommunityIdDomain) {
        getDashboardFragment(this)?.navigateToFragment(
            CommunityPageFragment.newInstance(communityId)
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
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
                                    val discussionIdModel = DiscussionIdModel(
                                        contentId.userId.userId,
                                        Permlink(contentId.permlink)
                                    )
                                    openPost(discussionIdModel, contentId)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        rvPosts.adapter = null
        super.onDestroyView()
    }

    private fun setupPostsList(rewardCurrency: RewardCurrency) {
        val profilePostAdapter = MyFeedAdapter(viewModel, PostItem.Type.PROFILE, viewModel.recordPostViewManager, rewardCurrency)
        val lManager = LinearLayoutManager(context)

        profilePostAdapter.click = { item ->
            item as PostItem
            val contentId = item.post.contentId
            val discussionIdModel = DiscussionIdModel(
                contentId.userId.userId,
                Permlink(contentId.permlink)
            )

            openPost(discussionIdModel, contentId)
        }

        with(rvPosts) {
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

            adapter = profilePostAdapter
        }

        profilePostAdapter.onPageRetryLoadingCallback = {
            viewModel.loadMorePosts()
        }
    }

    private fun observeViewModel() {
        viewModel.postsListState.observe(viewLifecycleOwner, Observer {
            val myFeedAdapter = rvPosts.adapter as MyFeedAdapter
            when (it) {
                is Paginator.State.Data<*> -> {
                    myFeedAdapter.hideLoadingNextPageError()
                    myFeedAdapter.hideLoadingNextPageProgress()
                    myFeedAdapter.updateMyFeedPosts(it.data as MutableList<Post>)
                    emptyPostProgressLoading.visibility = View.INVISIBLE
                }
                is Paginator.State.ItemUpdated<*> -> {
                    myFeedAdapter.updateMyFeedPosts(it.data as MutableList<Post>)
                }
                is Paginator.State.FullData<*> -> {
                    myFeedAdapter.hideLoadingNextPageError()
                    myFeedAdapter.hideLoadingNextPageProgress()
                    myFeedAdapter.updateMyFeedPosts(it.data as MutableList<Post>)
                    emptyPostProgressLoading.visibility = View.INVISIBLE
                }
                is Paginator.State.PageError<*> -> {
                    myFeedAdapter.hideLoadingNextPageProgress()
                    myFeedAdapter.showLoadingNextPageError()
                    rvPosts.scrollToPosition(myFeedAdapter.itemCount - 1)
                }
                is Paginator.State.NewPageProgress<*> -> {
                    myFeedAdapter.hideLoadingNextPageError()
                    myFeedAdapter.showLoadingNextPageProgress()
                }
                is Paginator.State.SearchProgress<*> -> {
                    myFeedAdapter.updateMyFeedPosts(it.data as MutableList<Post>)
                    myFeedAdapter.showLoadingNextPageProgress()
                    pbLoading.visibility = View.VISIBLE
                }
                is Paginator.State.SearchPageError<*> -> {
                    myFeedAdapter.updateMyFeedPosts(it.data as MutableList<Post>)
                    myFeedAdapter.hideLoadingNextPageProgress()
                    uiHelper.showMessage(R.string.loading_error)
                    pbLoading.visibility = View.INVISIBLE
                }
                is Paginator.State.Refresh<*> -> {
                    myFeedAdapter.clearAllPosts()
                    emptyPostProgressLoading.visibility = View.VISIBLE
                    btnRetry.visibility = View.INVISIBLE
                }
                is Paginator.State.EmptyProgress -> {
                    myFeedAdapter.updateMyFeedPosts(listOf())
                    emptyPostProgressLoading.visibility = View.VISIBLE
                    btnRetry.visibility = View.INVISIBLE
                }
                is Paginator.State.Empty -> {
                    emptyPostProgressLoading.visibility = View.INVISIBLE
                    myFeedAdapter.updateMyFeedPosts(mutableListOf())
                }
                is Paginator.State.EmptyError -> {
                    emptyPostProgressLoading.visibility = View.INVISIBLE
                    btnRetry.visibility = View.VISIBLE
                }
            }
        })

        viewModel.loadUserProgressVisibility.observe(viewLifecycleOwner, Observer {
            if (it) {
                userProgressLoading.visibility = View.VISIBLE
            } else {
                userProgressLoading.visibility = View.INVISIBLE
            }
        })

        viewModel.loadUserErrorVisibility.observe(viewLifecycleOwner, Observer {
            if (it) {
                btnRetry.visibility = View.VISIBLE
            } else {
                btnRetry.visibility = View.INVISIBLE
            }
        })

        viewModel.rewardCurrency.observe(viewLifecycleOwner, Observer {
            (rvPosts.adapter as MyFeedAdapter).updateRewardCurrency(it)
        })
    }

    private fun openPostReport(post: Post) {
        val tag = PostReportDialog::class.java.name
        if (childFragmentManager.findFragmentByTag(tag) == null) {
            val dialog = PostReportDialog.newInstance(PostReportDialog.Args(post.contentId))
            dialog.onPostReportCompleteCallback = {
                viewModel.sendReport(it)
            }
            dialog.show(childFragmentManager, tag)
        }
    }

    private fun editPost(post: Post) {
        startActivityForResult(
            EditorPageActivity.getIntent(
                requireContext(),
                EditorPageFragment.Args(
                    contentId = post.contentId
                )
            ),
            REQUEST_FOR_RESULT_FROM_EDIT
        )
    }

    private fun sharePost(shareUrl: String) {
        requireContext().shareMessage(shareUrl, viewModel.getCurrentUserId())
    }

    private fun openPostMenuDialog(postMenu: PostMenu) =
        PostPageMenuDialog.show(this,viewModel.isPostSubscriptionModified.value!!, postMenu) {
            when (it) {
                is PostPageMenuDialog.Result.AddFavorite -> viewModel.addToFavorite(it.postMenu.permlink)
                is PostPageMenuDialog.Result.ViewInExplorer-> viewModel.viewInExplorer(it.postMenu)
                is PostPageMenuDialog.Result.RemoveFavorite -> viewModel.removeFromFavorite(it.postMenu.permlink)
                is PostPageMenuDialog.Result.Share -> it.postMenu.shareUrl?.let { viewModel.onShareClicked(it) }
                is PostPageMenuDialog.Result.Edit -> viewModel.editPost(it.postMenu.permlink)
                is PostPageMenuDialog.Result.Delete -> viewModel.deletePost(it.postMenu.permlink, it.postMenu.communityId)
                is PostPageMenuDialog.Result.Subscribe -> viewModel.subscribeToCommunity(it.postMenu.communityId)
                is PostPageMenuDialog.Result.Unsubscribe -> viewModel.unsubscribeToCommunity(it.postMenu.communityId)
                is PostPageMenuDialog.Result.Report -> viewModel.onSendReportClicked(it.postMenu.permlink)
            }
        }

    private fun openPost(discussionIdModel: DiscussionIdModel, contentId: ContentIdDomain) {
        getDashboardFragment(this)?.navigateToFragment(
            PostPageFragment.newInstance(
                PostPageFragment.Args(
                    discussionIdModel,
                    contentId
                )
            ){
                viewModel.updatePostItem(it)
            },
            tag = contentId.permlink
        )
    }

    private fun viewInExplorer(exploreUrl: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(exploreUrl))
        startActivity(intent)
    }

    private fun showPostRewardDialog(@StringRes titleResId: Int, @StringRes textResId: Int) =
        PostRewardBottomSheetDialog.show(this, titleResId, textResId) {}

    private fun moveToDonate(command: NavigateToDonateCommand) =
        getDashboardFragment(this)?.navigateToFragment(
            DonateSendPointsFragment.newInstance(
                command.contentId,
                command.communityId,
                command.contentAuthor,
                command.balance,
                command.amount))

    private fun showDonationUsersDialogCommand(donations: DonationsDomain) = DonationUsersDialog.show(this, donations) {
        (it as? DonationUsersDialog.Result.ItemSelected)?.user?.let { viewModel.onUserClicked(it.userId) }
    }

    private fun showSelectRewardCurrencyDialog(currency: RewardCurrency) =
        SelectRewardCurrencyDialog.show(this, currency) {
            it?.rewardCurrency?.let { currency -> viewModel.updateRewardCurrency(currency) }
        }
}