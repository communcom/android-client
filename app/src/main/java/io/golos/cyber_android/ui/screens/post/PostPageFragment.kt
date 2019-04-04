package io.golos.cyber_android.ui.screens.post

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.golos.cyber_android.R
import io.golos.cyber_android.serviceLocator
import io.golos.cyber_android.ui.Tags
import io.golos.cyber_android.ui.common.comments.CommentsAdapter
import io.golos.cyber_android.ui.common.posts.AbstractFeedFragment
import io.golos.cyber_android.ui.screens.editor.EditorPageActivity
import io.golos.cyber_android.ui.screens.editor.EditorPageViewModel
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
        postCommentBottom.setOnClickListener {
            addRootComment()
        }

        feedList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                adjustInputVisibility((feedList.layoutManager as LinearLayoutManager).findLastVisibleItemPosition())
            }
        })
        postCommentParent.alpha = 0f
        postCommentParent.visibility = View.GONE
    }

    private fun addRootComment() {
        addCommentByParent(getDiscussionId())
    }

    private fun addCommentByParent(discussionId: DiscussionIdModel) {
        startActivity(
            EditorPageActivity.getIntent(
                requireContext(),
                EditorPageViewModel.Type.COMMENT,
                discussionId
            )
        )
    }

    private fun adjustInputVisibility(lastVisibleItem: Int) {
        if (lastVisibleItem > 0) {
            postCommentParent.animate()
                .alpha(1f)
                .setDuration(INPUT_ANIM_DURATION)
                .withStartAction {
                    postCommentParent.alpha = 0f
                    postCommentParent.visibility = View.VISIBLE
                }
                .start()
        } else {
            postCommentParent.animate()
                .alpha(0f)
                .setDuration(INPUT_ANIM_DURATION)
                .withStartAction {
                    postCommentParent.alpha = 1f
                }.withEndAction {
                    postCommentParent.visibility = View.GONE
                }
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
                addCommentByParent(comment.contentId)
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
        val id = getDiscussionId()
        viewModel = ViewModelProviders.of(
            this,
            requireActivity()
                .serviceLocator
                .getPostWithCommentsViewModelFactory(id)
        ).get(PostWithCommentsViewModel::class.java)
    }

    private fun getDiscussionId(): DiscussionIdModel {
        return DiscussionIdModel(
            arguments!!.getString(Tags.USER_ID)!!,
            arguments!!.getString(Tags.PERM_LINK)!!,
            arguments!!.getLong(Tags.REF_BLOCK_NUM)
        )
    }


}