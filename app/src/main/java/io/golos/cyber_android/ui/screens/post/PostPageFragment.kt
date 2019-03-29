package io.golos.cyber_android.ui.screens.post

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.golos.cyber_android.R
import io.golos.cyber_android.serviceLocator
import io.golos.cyber_android.ui.Tags
import io.golos.cyber_android.ui.common.comments.CommentsAdapter
import io.golos.cyber_android.ui.common.posts.AbstractFeedFragment
import io.golos.cyber_android.utils.DateUtils
import io.golos.domain.entities.CommentEntity
import io.golos.domain.interactors.model.CommentModel
import io.golos.domain.interactors.model.DiscussionIdModel
import io.golos.domain.interactors.model.PostModel
import io.golos.domain.model.CommentFeedUpdateRequest
import kotlinx.android.synthetic.main.fragment_post.*
import kotlinx.android.synthetic.main.header_post_card.*


const val INPUT_ANIM_DURATION = 400L

/**
 * Fragment for single [PostModel] presentation
 */
class PostPageFragment :
    AbstractFeedFragment<CommentFeedUpdateRequest, CommentEntity, CommentModel, PostWithCommentsViewModel>() {
    override val feedList: RecyclerView
        get() = postView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_post, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupViewModel()

        viewModel.postLiveData.observe(this, Observer {
            bindPostModel(it)
        })

        viewModel.loadingStatusLiveData.observe(this, Observer {
            showLoading()
        })

        postMenu.setColorFilter(Color.BLACK)
        back.setOnClickListener { activity?.finish() }

        feedList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val lastVisibleItem =
                    (feedList.layoutManager as LinearLayoutManager).findLastVisibleItemPosition()
                adjustInputVisibility(lastVisibleItem)
            }
        })
        postCommentParent.alpha = 0f
    }

    private fun adjustInputVisibility(lastVisibleItem: Int) {
        if (lastVisibleItem > 0) {
            (feedList.layoutParams as CoordinatorLayout.LayoutParams).bottomMargin =
                resources.getDimensionPixelSize(R.dimen.padding_bottom_comments_list)
                postCommentParent.animate()
                    .alpha(1f)
                    .setDuration(INPUT_ANIM_DURATION)
                    .start()
        } else {
                postCommentParent.animate()
                    .alpha(0f)
                    .withEndAction {
                        (feedList.layoutParams as CoordinatorLayout.LayoutParams).bottomMargin = 0
                    }.setDuration(INPUT_ANIM_DURATION)
                    .start()
        }
    }

    private fun showLoading() {

    }

    private fun bindPostModel(postModel: PostModel) {
        (feedList.adapter as PostPageAdapter).postModel = postModel
        bindToolbar(postModel)
    }

    private fun bindToolbar(postModel: PostModel) {
        //postAvatar
        //postMenu

        postAuthorName.text = postModel.community.name
        postAuthor.text = String.format(
            resources.getString(R.string.post_time_and_author_format),
            DateUtils.createTimeLabel(
                postModel.meta.time.time,
                postModel.meta.elapsedFormCreation.elapsedMinutes,
                postModel.meta.elapsedFormCreation.elapsedHours,
                postModel.meta.elapsedFormCreation.elapsedDays,
                requireContext()
            ),
            postModel.author.username
        )
    }

    override fun setupEventsProvider() {
    }

    override fun setupFeedAdapter() {
        feedList.adapter = PostPageAdapter(object : CommentsAdapter.Listener {
            override fun onCommentUpvote(comment: CommentModel) {
                viewModel.onUpvote(comment)
            }

            override fun onCommentDownvote(comment: CommentModel) {
                viewModel.onDownvote(comment)
            }

            override fun onReplyClick(comment: CommentModel) {
            }

        }, object : PostPageAdapter.Listener {
            override fun onPostUpvote(postModel: PostModel) {
                viewModel.onPostUpote()
            }

            override fun onPostDownvote(postModel: PostModel) {
                viewModel.onPostDownvote()
            }

        })
    }

    override fun onNewData() {
    }

    override fun setupWidgetsLiveData() {
    }

    override fun setupViewModel() {
        val id = DiscussionIdModel(
            arguments!!.getString(Tags.USER_ID)!!,
            arguments!!.getString(Tags.PERM_LINK)!!,
            arguments!!.getLong(Tags.REF_BLOCK_NUM)
        )
        viewModel = ViewModelProviders.of(
            this,
            requireActivity()
                .serviceLocator
                .getPostWithCommentsViewModelFactory(id)
        ).get(PostWithCommentsViewModel::class.java)
    }


}