package io.golos.cyber_android.ui.screens.feed_my.view

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.golos.cyber_android.R
import io.golos.cyber_android.application.App
import io.golos.cyber_android.databinding.FragmentMyFeedBinding
import io.golos.cyber_android.ui.dialogs.SelectRewardCurrencyDialog
import io.golos.cyber_android.ui.dialogs.donation.DonationUsersDialog
import io.golos.cyber_android.ui.dto.Post
import io.golos.domain.dto.RewardCurrency
import io.golos.cyber_android.ui.screens.community_page.view.CommunityPageFragment
import io.golos.cyber_android.ui.screens.donate_send_points.view.DonateSendPointsFragment
import io.golos.cyber_android.ui.screens.feed_my.di.MyFeedFragmentComponent
import io.golos.cyber_android.ui.screens.feed_my.dto.SwitchToProfileTab
import io.golos.cyber_android.ui.screens.feed_my.view.list.MyFeedAdapter
import io.golos.cyber_android.ui.screens.feed_my.view.view_commands.*
import io.golos.cyber_android.ui.screens.feed_my.view_model.MyFeedViewModel
import io.golos.cyber_android.ui.screens.post_edit.activity.EditorPageActivity
import io.golos.cyber_android.ui.screens.post_edit.fragment.view.EditorPageFragment
import io.golos.cyber_android.ui.screens.post_edit.fragment.view.EditorPageFragment.Companion.EXTRA_SHOULD_SHOW_IMAGE_PICKER_DIALOG
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
import io.golos.domain.dto.DonationsDomain
import io.golos.domain.dto.UserIdDomain
import io.golos.domain.use_cases.model.DiscussionIdModel
import kotlinx.android.synthetic.main.fragment_my_feed.*
import kotlinx.android.synthetic.main.view_search_bar.*
import timber.log.Timber

class MyFeedFragment : FragmentBaseMVVM<FragmentMyFeedBinding, MyFeedViewModel>() {
    companion object {
        private const val REQUEST_FOR_RESULT_FROM_EDIT = 41522
        private const val REQUEST_FOR_RESULT_FROM_CREATE_POST = 41523

        fun newInstance(): Fragment {
            return MyFeedFragment()
        }
    }

    override fun linkViewModel(binding: FragmentMyFeedBinding, viewModel: MyFeedViewModel) {
        binding.viewModel = viewModel
    }

    override fun provideViewModelType(): Class<MyFeedViewModel> = MyFeedViewModel::class.java

    override fun layoutResId(): Int = R.layout.fragment_my_feed

    override fun inject(key: String) = App.injections.get<MyFeedFragmentComponent>(key).inject(this)

    override fun releaseInjection(key: String) = App.injections.release<MyFeedFragmentComponent>(key)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupPostsList(viewModel.rewardCurrency.value!!)
        observeViewModel()
        viewModel.start()
    }

    private fun setupPostsList(rewardCurrency: RewardCurrency) {
        val myFeedAdapter = MyFeedAdapter(viewModel, PostItem.Type.FEED, viewModel.recordPostViewManager, rewardCurrency)
        myFeedAdapter.click = { item ->
            when(item){
                is PostItem -> {
                    val contentId = item.post.contentId
                    viewModel.onPostClicked(contentId)
                }
//                is CreatePostItem -> {
//                    viewModel.onCreatePostClicked()
//                }
                else -> Timber.e("Undefined item in adapter {${MyFeedAdapter::class.java}}")
            }
        }
        val lManager = LinearLayoutManager(context)

        rvPosts.addItemDecoration(DividerPostDecoration(requireContext()))
        rvPosts.layoutManager = lManager

        rvPosts.addOnScrollListener(object : RecyclerView.OnScrollListener() {
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

        rvPosts.adapter = myFeedAdapter
        myFeedAdapter.onPageRetryLoadingCallback = {
            viewModel.loadMorePosts()
        }
    }

    override fun processViewCommand(command: ViewCommand) {
        when (command) {
            is NavigateToImageViewCommand -> requireContext().openImageView(command.imageUri)
            is NavigateToLinkViewCommand -> requireContext().openLinkView(command.link)
            is NavigateToUserProfileCommand -> openUserProfile(command.userId)
            is NavigateToCommunityPageCommand -> openCommunityPage(command.communityId)
            is NavigateToPostCommand -> openPost(command.discussionIdModel, command.contentId)
            is NavigationToPostMenuViewCommand -> openPostMenuDialog(command.post)
            is ViewInExplorerViewCommand -> viewInExplorer(command.exploreUrl)
            is SharePostCommand -> sharePost(command.shareUrl)
            is EditPostCommand -> editPost(command.post)
            is ReportPostCommand -> openPostReportDialog(command.post)
            is CreatePostCommand -> createPost()
            is SwitchToProfileTab -> switchToProfileTab()
            is NavigateToDonateCommand -> moveToDonate(command)
            is ShowDonationUsersDialogCommand -> showDonationUsersDialogCommand(command.donation)
            is SelectRewardCurrencyDialogCommand -> showSelectRewardCurrencyDialog(command.startCurrency)
        }
    }

    private fun createPost(){
        startActivityForResult(
            EditorPageActivity.getIntent(requireContext()).putExtra(EXTRA_SHOULD_SHOW_IMAGE_PICKER_DIALOG, true),
            REQUEST_FOR_RESULT_FROM_CREATE_POST
        )
    }

    private fun viewInExplorer(exploreUrl: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(exploreUrl))
        startActivity(intent)
    }

    private fun openUserProfile(userId: UserIdDomain) {
        getDashboardFragment(this)?.navigateToFragment(
            ProfileExternalUserFragment.newInstance(userId)
        )
    }

    private fun openCommunityPage(communityId: CommunityIdDomain) {
        getDashboardFragment(this)?.navigateToFragment(
            CommunityPageFragment.newInstance(communityId)
        )
    }

    private fun switchToProfileTab() {
        getDashboardFragment(this)?.switchToProfilePage()
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
        requireContext().shareMessage(shareUrl)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
//            PostPageMenuDialog.REQUEST -> {
//                when (resultCode) {
//                    PostPageMenuDialog.RESULT_ADD_FAVORITE -> {
//                        val postMenu: PostMenu? = data?.extras?.getParcelable(Tags.POST_MENU)
//                        postMenu?.let {
//                            viewModel.addToFavorite(it.permlink)
//                        }
//                    }
//                    PostPageMenuDialog.RESULT_REMOVE_FAVORITE -> {
//                        val postMenu: PostMenu? = data?.extras?.getParcelable(Tags.POST_MENU)
//                        postMenu?.let {
//                            viewModel.removeFromFavorite(it.permlink)
//                        }
//                    }
//                    PostPageMenuDialog.RESULT_SHARE -> {
//                        val postMenu: PostMenu? = data?.extras?.getParcelable(Tags.POST_MENU)
//                        val shareUrl = postMenu?.shareUrl
//                        shareUrl?.let {
//                            viewModel.onShareClicked(it)
//                        }
//                    }
//                    PostPageMenuDialog.RESULT_EDIT -> {
//                        val postMenu: PostMenu? = data?.extras?.getParcelable(Tags.POST_MENU)
//                        postMenu?.let {
//                            viewModel.editPost(it.permlink)
//                        }
//                    }
//                    PostPageMenuDialog.RESULT_DELETE -> {
//                        val postMenu: PostMenu? = data?.extras?.getParcelable(Tags.POST_MENU)
//                        postMenu?.let { menu ->
//                            viewModel.deletePost(menu.permlink, menu.communityId)
//                        }
//                    }
//                    PostPageMenuDialog.RESULT_SUBSCRIBE -> {
//                        val postMenu: PostMenu? = data?.extras?.getParcelable(Tags.POST_MENU)
//                        val communityId = postMenu?.communityId
//                        communityId?.let {
//                            viewModel.subscribeToCommunity(it)
//                        }
//                    }
//                    PostPageMenuDialog.RESULT_UNSUBSCRIBE -> {
//                        val postMenu: PostMenu? = data?.extras?.getParcelable(Tags.POST_MENU)
//                        postMenu?.communityId?.let {
//                            viewModel.unsubscribeToCommunity(it)
//                        }
//                    }
//                    PostPageMenuDialog.RESULT_REPORT -> {
//                        val postMenu: PostMenu? = data?.extras?.getParcelable(Tags.POST_MENU)
//                        postMenu?.let {
//                            viewModel.onReportPostClicked(it.permlink)
//                        }
//                    }
//                }
//            }
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
            REQUEST_FOR_RESULT_FROM_CREATE_POST -> {
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

    private fun openPostMenuDialog(postMenu: PostMenu) {
        PostPageMenuDialog.show(this,viewModel.isPostSubscriptionModified.value!!, postMenu) {
            when (it) {
                is PostPageMenuDialog.Result.ViewInExplorer->viewModel.viewInExplorer(it.postMenu)
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

    private fun openPost(
        discussionIdModel: DiscussionIdModel,
        contentId: ContentIdDomain
    ) {
        getDashboardFragment(this)?.navigateToFragment(
            PostPageFragment.newInstance(
                PostPageFragment.Args(
                    discussionIdModel,
                    contentId
                )
            ),
            tag = contentId.permlink
        )
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
                    Timber.d("filter: GET new posts after filter update")
                    myFeedAdapter.clearAllPosts()
                    emptyPostProgressLoading.visibility = View.VISIBLE
                    btnRetry.visibility = View.INVISIBLE
                }
                is Paginator.State.EmptyProgress -> {
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
        viewModel.user.observe(viewLifecycleOwner, Observer {
            val myFeedAdapter = rvPosts.adapter as MyFeedAdapter
            myFeedAdapter.updateUser(
                it,
                onCreatePostClick = { viewModel.onCreatePostClicked() },
                onUserClick = { viewModel.onCurrentUserClicked() })
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

    override fun onDestroyView() {
        rvPosts.adapter = null
        super.onDestroyView()
    }

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