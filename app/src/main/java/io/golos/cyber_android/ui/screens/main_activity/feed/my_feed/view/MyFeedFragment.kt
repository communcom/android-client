package io.golos.cyber_android.ui.screens.main_activity.feed.my_feed.view

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
import io.golos.cyber_android.ui.Tags
import io.golos.cyber_android.ui.common.ImageViewerActivity
import io.golos.cyber_android.ui.common.mvvm.FragmentBaseMVVM
import io.golos.cyber_android.ui.common.mvvm.view_commands.ViewCommand
import io.golos.cyber_android.ui.common.paginator.Paginator
import io.golos.cyber_android.ui.common.utils.DividerPostDecoration
import io.golos.cyber_android.ui.dialogs.post.PostPageMenuDialog
import io.golos.cyber_android.ui.dialogs.post.model.PostMenu
import io.golos.cyber_android.ui.dto.Post
import io.golos.cyber_android.ui.screens.main_activity.feed.my_feed.di.MyFeedFragmentComponent
import io.golos.cyber_android.ui.screens.main_activity.feed.my_feed.view.list.MyFeedAdapter
import io.golos.cyber_android.ui.screens.main_activity.feed.my_feed.view.view_commands.*
import io.golos.cyber_android.ui.screens.main_activity.feed.my_feed.view_model.MyFeedViewModel
import io.golos.cyber_android.ui.screens.profile.old_profile.ProfileActivity
import io.golos.cyber_android.ui.shared_fragments.post.view.PostActivity
import io.golos.cyber_android.ui.shared_fragments.post.view.PostPageFragment
import io.golos.domain.use_cases.model.DiscussionIdModel
import kotlinx.android.synthetic.main.fragment_my_feed.*
import kotlinx.android.synthetic.main.view_search_bar.*
import timber.log.Timber

class MyFeedFragment : FragmentBaseMVVM<FragmentMyFeedBinding, MyFeedViewModel>() {

    override fun linkViewModel(binding: FragmentMyFeedBinding, viewModel: MyFeedViewModel) {
        binding.viewModel = viewModel
    }

    override fun provideViewModelType(): Class<MyFeedViewModel> = MyFeedViewModel::class.java

    override fun layoutResId(): Int = R.layout.fragment_my_feed

    override fun inject() = App.injections.get<MyFeedFragmentComponent>()
        .inject(this)

    override fun releaseInjection() {
        App.injections.release<MyFeedFragmentComponent>()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupPostsList()
        observeViewModel()
        viewModel.start()
    }

    private fun setupPostsList() {
        val myFeedAdapter = MyFeedAdapter(viewModel)
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
            is NavigateToImageViewCommand -> openImageView(command.imageUri)

            is NavigateToLinkViewCommand -> openLinkView(command.link)

            is NavigateToUserProfileViewCommand -> openUserProfile(command.userId)

            is NavigateToPostCommand -> openPost(command.discussionIdModel)

            is NavigationToPostMenuViewCommand -> openPostMenuDialog(command.post)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            PostPageMenuDialog.REQUEST -> {
                when (resultCode) {
                    PostPageMenuDialog.RESULT_ADD_FAVORITE -> viewModel.addToFavorite()
                    PostPageMenuDialog.RESULT_REMOVE_FAVORITE -> viewModel.removeFromFavorite()
                    PostPageMenuDialog.RESULT_SHARE -> {
                        val shareUrl = data?.extras?.getString(Tags.SHARE_URL)
                        shareUrl?.let { url ->
                            viewModel.sharePost(url)
                        }
                    }
                    PostPageMenuDialog.RESULT_EDIT -> viewModel.editPost()
                    PostPageMenuDialog.RESULT_DELETE -> viewModel.deletePost()
                    PostPageMenuDialog.RESULT_JOIN -> {
                        val communityId = data?.extras?.getString(Tags.COMMUNITY_ID)
                        communityId?.let { id ->
                            viewModel.joinToCommunity(id)
                        }
                    }
                    PostPageMenuDialog.RESULT_JOINED -> {
                        val communityId = data?.extras?.getString(Tags.COMMUNITY_ID)
                        communityId?.let { id ->
                            viewModel.joinedToCommunity(id)
                        }
                    }
                    PostPageMenuDialog.RESULT_REPORT -> viewModel.reportPost()
                }
            }
        }
    }

    private fun openPostMenuDialog(postMenu: PostMenu) {
        PostPageMenuDialog.newInstance(postMenu).apply {
            setTargetFragment(this@MyFeedFragment, PostPageMenuDialog.REQUEST)
        }.show(requireFragmentManager(), "show")
    }

    private fun openPost(discussionIdModel: DiscussionIdModel) {
        startActivity(PostActivity.getIntent(requireContext(), PostPageFragment.Args(discussionIdModel)))
    }

    private fun openUserProfile(userId: String) = startActivity(ProfileActivity.getIntent(requireContext(), userId))

    private fun openLinkView(link: Uri) {
        Intent(Intent.ACTION_VIEW, link)
            .also { intent ->
                if (intent.resolveActivity(requireActivity().packageManager) != null) {
                    startActivity(intent)
                }
            }
    }

    private fun openImageView(imageUri: Uri) =
        startActivity(ImageViewerActivity.getIntent(requireContext(), imageUri.toString()))

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
            myFeedAdapter.updateUser(it)
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

    override fun onDestroyView() {
        rvPosts.adapter = null
        super.onDestroyView()
    }

    companion object {

        fun newInstance(): Fragment {

            return MyFeedFragment()
        }
    }
}