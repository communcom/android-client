package io.golos.cyber_android.ui.screens.community_page.child_pages.community_post.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.golos.cyber_android.R
import io.golos.cyber_android.application.App
import io.golos.cyber_android.databinding.FragmentCommunityPostBinding
import io.golos.cyber_android.ui.common.mvvm.FragmentBaseMVVM
import io.golos.cyber_android.ui.common.mvvm.view_commands.ViewCommand
import io.golos.cyber_android.ui.common.paginator.Paginator
import io.golos.cyber_android.ui.common.widgets.post_comments.items.PostItem
import io.golos.cyber_android.ui.dto.ContentId
import io.golos.cyber_android.ui.dto.Post
import io.golos.cyber_android.ui.screens.community_page.child_pages.community_post.di.CommunityPostFragmentComponent
import io.golos.cyber_android.ui.screens.community_page.child_pages.community_post.view_model.CommunityPostViewModel
import io.golos.cyber_android.ui.screens.my_feed.view.list.MyFeedAdapter
import io.golos.cyber_android.ui.shared_fragments.post.view.PostActivity
import io.golos.cyber_android.ui.shared_fragments.post.view.PostPageFragment
import io.golos.cyber_android.ui.utils.DividerPostDecoration
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

    override fun inject() = App.injections.get<CommunityPostFragmentComponent>(
        arguments!!.getString(COMMUNITY_ID_EXTRA)
    ).inject(this)

    override fun releaseInjection() {
        App.injections.release<CommunityPostFragmentComponent>()
    }

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
    }

    override fun processViewCommand(command: ViewCommand) {
        when (command) {

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
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
        viewModel.postsListState.observe(viewLifecycleOwner, Observer { state ->
            val postAdapter = rvCommunityPosts.adapter as MyFeedAdapter
            when (state) {
                is Paginator.State.Data<*> -> {
                    postAdapter.hideLoadingNextPageError()
                    postAdapter.hideLoadingNextPageProgress()
                    postAdapter.updateMyFeedPosts(state.data as MutableList<Post>)
                    emptyPostProgressLoading.visibility = View.INVISIBLE
                }
                is Paginator.State.FullData<*> -> {
                    postAdapter.hideLoadingNextPageError()
                    postAdapter.hideLoadingNextPageProgress()
                    emptyPostProgressLoading.visibility = View.INVISIBLE
                }
                is Paginator.State.PageError<*> -> {
                    postAdapter.hideLoadingNextPageProgress()
                    postAdapter.showLoadingNextPageError()
                    rvPosts.scrollToPosition(postAdapter.itemCount - 1)
                }
                is Paginator.State.NewPageProgress<*> -> {
                    postAdapter.hideLoadingNextPageError()
                    postAdapter.showLoadingNextPageProgress()
                    rvPosts.scrollToPosition(postAdapter.itemCount - 1)
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
                }
                is Paginator.State.EmptyProgress -> {
                    emptyPostProgressLoading.visibility = View.VISIBLE
                    btnRetry.visibility = View.INVISIBLE
                }
                is Paginator.State.Empty -> {
                    emptyPostProgressLoading.visibility = View.INVISIBLE
                    postAdapter.updateMyFeedPosts(mutableListOf())
                }
                is Paginator.State.EmptyError -> {
                    emptyPostProgressLoading.visibility = View.INVISIBLE
                    btnRetry.visibility = View.VISIBLE
                }
            }
        })
    }

    private fun openPost(
        discussionIdModel: DiscussionIdModel,
        contentId: ContentId
    ) {
        startActivityForResult(
            PostActivity.getIntent(
                requireContext(),
                PostPageFragment.Args(
                    discussionIdModel,
                    contentId
                )
            ),
            UPDATED_REQUEST_CODE
        )
    }

    override fun onDestroy() {
        rvCommunityPosts.adapter = null
        super.onDestroy()
    }

    companion object {

        private const val COMMUNITY_ID_EXTRA = "community_id"

        private const val UPDATED_REQUEST_CODE = 41245

        fun newInstance(
            communityId: String
        ): CommunityPostFragment = CommunityPostFragment().apply {
            arguments = Bundle().apply {
                putString(COMMUNITY_ID_EXTRA, communityId)
            }
        }

    }
}