package io.golos.cyber_android.ui.screens.profile_posts.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.golos.cyber_android.R
import io.golos.cyber_android.application.App
import io.golos.cyber_android.databinding.FragmentProfilePostsBinding
import io.golos.cyber_android.ui.Tags
import io.golos.cyber_android.ui.common.ImageViewerActivity
import io.golos.cyber_android.ui.common.mvvm.FragmentBaseMVVM
import io.golos.cyber_android.ui.common.mvvm.view_commands.ViewCommand
import io.golos.cyber_android.ui.common.paginator.Paginator
import io.golos.cyber_android.ui.dto.Post
import io.golos.cyber_android.ui.screens.editor_page_activity.EditorPageActivity
import io.golos.cyber_android.ui.screens.my_feed.view.items.PostItem
import io.golos.cyber_android.ui.screens.my_feed.view.list.MyFeedAdapter
import io.golos.cyber_android.ui.screens.post_page_menu.model.PostMenu
import io.golos.cyber_android.ui.screens.post_page_menu.view.PostPageMenuDialog
import io.golos.cyber_android.ui.screens.post_report.view.PostReportDialog
import io.golos.cyber_android.ui.screens.profile.new_profile.view.ProfileExternalUserFragment
import io.golos.cyber_android.ui.screens.profile_posts.di.ProfilePostsFragmentComponent
import io.golos.cyber_android.ui.screens.profile_posts.view_commands.*
import io.golos.cyber_android.ui.screens.profile_posts.view_model.ProfilePostsViewModel
import io.golos.cyber_android.ui.shared_fragments.editor.view.EditorPageFragment
import io.golos.cyber_android.ui.shared_fragments.post.view.PostActivity
import io.golos.cyber_android.ui.shared_fragments.post.view.PostPageFragment
import io.golos.cyber_android.ui.utils.DividerPostDecoration
import io.golos.cyber_android.ui.utils.shareMessage
import io.golos.domain.commun_entities.Permlink
import io.golos.domain.dto.PostsConfigurationDomain
import io.golos.domain.dto.UserIdDomain
import io.golos.domain.use_cases.model.DiscussionIdModel
import kotlinx.android.synthetic.main.fragment_profile_posts.*
import kotlinx.android.synthetic.main.view_search_bar.*

open class ProfilePostsFragment : FragmentBaseMVVM<FragmentProfilePostsBinding, ProfilePostsViewModel>() {
    companion object {
        fun newInstance() = ProfilePostsFragment()
    }

    override fun linkViewModel(binding: FragmentProfilePostsBinding, viewModel: ProfilePostsViewModel) {
        binding.viewModel = viewModel
    }

    override fun provideViewModelType(): Class<ProfilePostsViewModel> = ProfilePostsViewModel::class.java

    override fun layoutResId(): Int = R.layout.fragment_profile_posts

    override fun inject() {
        App.injections
            .get<ProfilePostsFragmentComponent>(PostsConfigurationDomain.TypeFeedDomain.BY_USER)
            .inject(this)
    }

    override fun releaseInjection() {
        App.injections.release<ProfilePostsFragmentComponent>()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupPostsList()
        observeViewModel()
        viewModel.start()
    }

    override fun processViewCommand(command: ViewCommand) {
        when (command) {
            is NavigateToImageViewCommand -> openImageView(command.imageUri)

            is NavigateToLinkViewCommand -> openLinkView(command.link)

            is NavigateToUserProfileViewCommand -> openUserProfile(command.userId)

            is NavigateToPostCommand -> openPost(command.discussionIdModel, command.contentId)

            is NavigationToPostMenuViewCommand -> openPostMenuDialog(command.post)

            is SharePostCommand -> sharePost(command.shareUrl)

            is EditPostCommand -> editPost(command.post)

            is ReportPostCommand -> openPostReport(command.post)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
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
                        postMenu?.let {
                            viewModel.deletePost(it.permlink)
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
                            viewModel.onSendReportClicked(it.permlink)
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

    private fun setupPostsList() {
        val profilePostAdapter = MyFeedAdapter(viewModel, PostItem.Type.PROFILE)
        val lManager = LinearLayoutManager(context)

        profilePostAdapter.click = { item ->
            item as PostItem
            val contentId = item.post.contentId
            val discussionIdModel = DiscussionIdModel(
                contentId.userId,
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
                    rvPosts.scrollToPosition(myFeedAdapter.itemCount - 1)
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

        viewModel.loadUserProgressVisibility.observe(this, Observer {
            if (it) {
                userProgressLoading.visibility = View.VISIBLE
            } else {
                userProgressLoading.visibility = View.INVISIBLE
            }
        })

        viewModel.loadUserErrorVisibility.observe(this, Observer {
            if (it) {
                btnRetry.visibility = View.VISIBLE
            } else {
                btnRetry.visibility = View.INVISIBLE
            }
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
        startActivity(
            EditorPageActivity.getIntent(
                requireContext(),
                EditorPageFragment.Args(
                    contentId = post.contentId
                )
            )
        )
    }

    private fun sharePost(shareUrl: String) {
        requireContext().shareMessage(shareUrl)
    }

    private fun openPostMenuDialog(postMenu: PostMenu) {
        PostPageMenuDialog.newInstance(postMenu).apply {
            setTargetFragment(this@ProfilePostsFragment, PostPageMenuDialog.REQUEST)
        }.show(requireFragmentManager(), "show")
    }

    private fun openPost(discussionIdModel: DiscussionIdModel, contentId: Post.ContentId) {
        startActivity(
            PostActivity.getIntent(
                requireContext(),
                PostPageFragment.Args(
                    discussionIdModel,
                    contentId
                )
            )
        )
    }

    private fun openUserProfile(userId: String) {
        getDashboardFragment(this)?.showFragment(ProfileExternalUserFragment.newInstance(UserIdDomain(userId)))
    }

    private fun openLinkView(link: Uri) {
        Intent(Intent.ACTION_VIEW, link)
            .also { intent ->
                if (intent.resolveActivity(requireActivity().packageManager) != null) {
                    startActivity(intent)
                }
            }
    }

    private fun openImageView(imageUri: Uri) {
        startActivity(ImageViewerActivity.getIntent(requireContext(), imageUri.toString()))
    }
}