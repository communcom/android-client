package io.golos.cyber_android.ui.screens.post

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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
import io.golos.cyber_android.widgets.CommentWidget
import io.golos.domain.entities.CommentEntity
import io.golos.domain.interactors.model.CommentModel
import io.golos.domain.interactors.model.DiscussionIdModel
import io.golos.domain.interactors.model.PostModel
import io.golos.domain.model.CommentFeedUpdateRequest
import io.golos.domain.model.QueryResult
import kotlinx.android.synthetic.main.fragment_post.*
import kotlinx.android.synthetic.main.header_post_card.*


const val INPUT_ANIM_DURATION = 400L

const val GALLERY_REQUEST = 101

/**
 * Fragment for single [PostModel] presentation
 */
class PostPageFragment :
    AbstractFeedFragment<CommentFeedUpdateRequest, CommentEntity, CommentModel, PostPageViewModel>() {
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
        setupCommentWidget()
        observeViewModel()

        postMenu.setColorFilter(Color.BLACK)
        back.setOnClickListener { activity?.finish() }

        feedList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                adjustInputVisibility((feedList.layoutManager as LinearLayoutManager).findLastVisibleItemPosition())
            }
        })
        postCommentBottom.visibility = View.GONE
        postCommentBottom.alpha = 0f
    }

    private fun observeViewModel() {
        viewModel.postLiveData.observe(this, Observer {
            bindPostModel(it)
        })

        viewModel.loadingStatusLiveData.observe(this, Observer {
            showFeedLoading()
        })

        viewModel.discussionCreationLiveData.observe(this, Observer {
            it.getIfNotHandled()?.let { result ->
                when (result) {
                    is QueryResult.Loading<*> -> showLoading()
                    is QueryResult.Success<*> -> {
                        hideLoading()
                        postCommentBottom.clearText()
                    }
                    is QueryResult.Error<*> -> {
                        hideLoading()
                        Toast.makeText(requireContext(), "Post creation failed", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })

        viewModel.discussionToReplyLiveData.observe(this, Observer {
            postCommentBottom.setUserToReply(it?.userId)
        })

        viewModel.commentValidnessLiveData.observe(this, Observer {
            postCommentBottom.isSendEnabled = it
        })

        viewModel.commentInputVisibilityLiveData.observe(this, Observer {
            setCommentInputVisibility(it)
        })
    }

    private fun setupCommentWidget() {
        postCommentBottom.listener = object : CommentWidget.Listener {
            override fun onCommentChanged(text: String) {
                viewModel.onCommentChanged(text)
            }

            override fun onUserNameCleared() {
                viewModel.clearDiscussionToReply()
            }

            override fun onSendClick(text: String) {
                viewModel.sendComment(text)
            }

            override fun onGalleryClick() {
                val intent = Intent(
                    Intent.ACTION_PICK,
                    MediaStore.Images.Media.INTERNAL_CONTENT_URI
                )
                intent.type = "image/*"
                startActivityForResult(intent, GALLERY_REQUEST)
            }
        }
    }

    private fun addCommentByParent(discussionId: DiscussionIdModel) {
        viewModel.setDiscussionToReply(discussionId)
    }

    private fun adjustInputVisibility(lastVisibleItem: Int) {
        if (lastVisibleItem > 0) {
            viewModel.setCommentInputVisibility(PostPageViewModel.Visibility.VISIBLE)
        } else {
            viewModel.setCommentInputVisibility(PostPageViewModel.Visibility.GONE)
        }
    }

    private fun setCommentInputVisibility(visibility: PostPageViewModel.Visibility) {
        if (visibility == PostPageViewModel.Visibility.VISIBLE) {
            if (postCommentBottom.alpha == 0f)
                postCommentBottom.animate()
                    .alpha(1f)
                    .setDuration(INPUT_ANIM_DURATION)
                    .withStartAction {
                        postCommentBottom.visibility = View.VISIBLE
                    }
                    .start()
        } else {
            if (postCommentBottom.alpha == 1f)
                postCommentBottom.animate()
                    .alpha(0f)
                    .setDuration(INPUT_ANIM_DURATION)
                    .withEndAction {
                        postCommentBottom.visibility = View.GONE
                    }
                    .start()
        }
    }

    private fun showFeedLoading() {

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
                viewModel.onPostUpvote()
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
        ).get(PostPageViewModel::class.java)
    }

    private fun getDiscussionId(): DiscussionIdModel {
        return requireContext()
            .serviceLocator
            .moshi
            .adapter(DiscussionIdModel::class.java)
            .fromJson(arguments!!.getString(Tags.DISCUSSION_ID)!!)!!
    }


}