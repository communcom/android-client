package io.golos.cyber_android.ui.screens.post

import android.content.Intent
import android.graphics.Color
import android.net.Uri
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
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import io.golos.cyber_android.R
import io.golos.cyber_android.serviceLocator
import io.golos.cyber_android.ui.Tags
import io.golos.cyber_android.ui.common.ImageViewerActivity
import io.golos.cyber_android.ui.common.comments.CommentsAdapter
import io.golos.cyber_android.ui.common.posts.AbstractFeedFragment
import io.golos.cyber_android.ui.dialogs.ConfirmationDialog
import io.golos.cyber_android.ui.dialogs.NotificationDialog
import io.golos.cyber_android.ui.dialogs.PostPageMenuDialog
import io.golos.cyber_android.ui.screens.editor.EditorPageActivity
import io.golos.cyber_android.ui.screens.editor.EditorPageFragment
import io.golos.cyber_android.ui.screens.post.adapter.PostPageAdapter
import io.golos.cyber_android.ui.screens.profile.ProfileActivity
import io.golos.cyber_android.utils.DateUtils
import io.golos.cyber_android.views.utils.ViewUtils
import io.golos.cyber_android.widgets.CommentWidget
import io.golos.data.errors.CannotDeleteDiscussionWithChildCommentsException
import io.golos.domain.entities.CommentEntity
import io.golos.domain.interactors.model.*
import io.golos.domain.requestmodel.CommentFeedUpdateRequest
import io.golos.domain.requestmodel.QueryResult
import kotlinx.android.synthetic.main.fragment_post.*
import kotlinx.android.synthetic.main.header_post_card.*


const val INPUT_ANIM_DURATION = 400L

const val GALLERY_REQUEST = 101
const val POST_MENU_REQUEST = 102

/**
 * Fragment for single [PostModel] presentation
 */
class PostPageFragment :
    AbstractFeedFragment<CommentFeedUpdateRequest, CommentEntity, CommentModel, PostPageViewModel>() {

    data class Args(val id: DiscussionIdModel, val scrollToComments: Boolean = false)

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


        viewModel.getFullyLoadedLiveData.observe(this, Observer { isLoaded ->
            if (isLoaded) scrollToCommentsIfNeeded()
        })

        viewModel.discussionCreationLiveData.observe(this, Observer {
            it.getIfNotHandled()?.let { result ->
                when (result) {
                    is QueryResult.Loading -> showLoading()
                    is QueryResult.Success -> {
                        when (result.originalQuery) {
                            is CommentCreationResultModel -> {
                                hideLoading()
                                postCommentBottom.clearText()
                                viewModel.clearDiscussionToReply()
                            }

                            is DeleteDiscussionResultModel -> {
                                Toast.makeText(requireContext(), "Deleted successfully", Toast.LENGTH_SHORT).show()
                                requireActivity().finish()
                            }
                        }

                    }
                    is QueryResult.Error -> {
                        hideLoading()
                        Toast.makeText(requireContext(), "${result.error::class} e1 = ${result.error.message}", Toast.LENGTH_SHORT).show()
                        when (result.error) {
                            is CannotDeleteDiscussionWithChildCommentsException ->
                                NotificationDialog.newInstance(getString(R.string.cant_delete_discussion_with_child_comments))
                                    .show(requireFragmentManager(), "delete error")

                            else -> Toast.makeText(requireContext(), "Post creation failed", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        })

        viewModel.getDiscussionToReplyLiveData.observe(this, Observer {
            if (it != null)
                postCommentBottom.setUserToReply("@${it.userId}")
            else postCommentBottom.clearText()
        })

        viewModel.getCommentValidnessLiveData.observe(this, Observer {
            postCommentBottom.isSendEnabled = it
        })

        viewModel.getCommentInputVisibilityLiveData.observe(this, Observer {
            setCommentInputVisibility(it)
        })

        viewModel.getIsMyPostLiveData.observe(this, Observer { isMyPost ->
            postMenu.visibility = if (isMyPost) View.VISIBLE else View.GONE

            postMenu.setOnClickListener {
                PostPageMenuDialog.newInstance(isMyPost).apply {
                    setTargetFragment(this@PostPageFragment, POST_MENU_REQUEST)
                }.show(requireFragmentManager(), "menu")
            }
        })
    }

    private var scrolled = false

    private fun scrollToCommentsIfNeeded() {
        if (getArgs().scrollToComments
            && !scrolled
        ) {
            //need to slightly delay the scrolling due to adapter updating
            feedList.postDelayed({
                (feedList.layoutManager as? LinearLayoutManager)?.scrollToPositionWithOffset(
                    (feedList.adapter as PostPageAdapter).getCommentsTitlePosition(),
                    0
                )
            }, 700)
            scrolled = true
        }
    }

    private fun setupCommentWidget() {
        postCommentBottom.listener = object : CommentWidget.Listener {
            override fun onCommentChanged(text: CharSequence) {
                viewModel.onCommentChanged(text)
            }

            override fun onUserNameCleared() {
                viewModel.clearDiscussionToReply()
            }

            override fun onSendClick(text: CharSequence) {
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
        if (lastVisibleItem >= (feedList.adapter as PostPageAdapter).getCommentsTitlePosition()) {
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
            if (postCommentBottom.alpha == 1f) {
                postCommentBottom.animate()
                    .alpha(0f)
                    .setDuration(INPUT_ANIM_DURATION)
                    .withEndAction {
                        postCommentBottom?.visibility = View.GONE
                    }
                    .start()
                ViewUtils.hideKeyboard(requireActivity())
            }
        }
    }


    override fun onPause() {
        super.onPause()
        postCommentBottom.clearAnimation()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == POST_MENU_REQUEST) {
            when (resultCode) {
                PostPageMenuDialog.RESULT_EDIT -> editPost()
                PostPageMenuDialog.RESULT_DELETE -> deletePost()
            }
        }
    }

    private fun editPost() {
        viewModel.postLiveData.value?.let { post ->
            startActivity(EditorPageActivity.getIntent(requireContext(), EditorPageFragment.Args(post.contentId)))
        }
    }

    private fun deletePost() {
        ConfirmationDialog.newInstance(getString(R.string.delete_post_confirmation)).run {
            listener = viewModel::deletePost
            show(this@PostPageFragment.requireFragmentManager(), "confirm")
        }
    }

    private fun showFeedLoading() {

    }

    private fun bindPostModel(postModel: PostModel) {
        (feedList.adapter as PostPageAdapter).postModel = postModel
        bindToolbar(postModel)
    }

    private fun bindToolbar(postModel: PostModel) {
        //postMenu
        if (postModel.author.avatarUrl.isNotBlank())
            Glide.with(requireContext())
                .load(postModel.author.avatarUrl)
                .apply(RequestOptions.circleCropTransform())
                .into(postAvatar)
        else
            Glide.with(requireContext())
                .load(R.drawable.img_example_avatar)
                .apply(RequestOptions.circleCropTransform())
                .into(postAvatar)

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

        postHeaderLayout.setOnClickListener {
            startActivity(ProfileActivity.getIntent(requireContext(), postModel.author.userId.userId))
        }
    }

    override fun setupEventsProvider() {
    }

    override fun setupFeedAdapter() {
        feedList.adapter =
            PostPageAdapter(viewLifecycleOwner,
                object : CommentsAdapter.Listener {
                    override fun onUsernameClick(userId: String) {
                        startActivity(ProfileActivity.getIntent(requireContext(), userId))
                    }

                    override fun onImageLinkClick(url: String) {
                        startActivity(ImageViewerActivity.getIntent(requireContext(), url))
                    }

                    override fun onWebLinkClick(url: String) {
                        val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        if (webIntent.resolveActivity(requireActivity().packageManager) != null) {
                            startActivity(webIntent)
                        }
                    }

                    override fun onCommentUpvote(comment: CommentModel) {
                        viewModel.onUpvote(comment)
                    }

                    override fun onCommentDownvote(comment: CommentModel) {
                        viewModel.onDownvote(comment)
                    }

                    override fun onReplyClick(comment: CommentModel) {
                        addCommentByParent(comment.contentId)
                        (feedList.adapter as PostPageAdapter).scrollToComment(
                            comment,
                            feedList
                        )
                    }

                },
                object : PostPageAdapter.Listener {
                    override fun onPostUpvote(postModel: PostModel) {
                        viewModel.onPostUpvote()
                    }

                    override fun onPostDownvote(postModel: PostModel) {
                        viewModel.onPostDownvote()
                    }

                })
    }

    override fun onNewData(data: List<CommentModel>) {
    }

    override fun setupWidgetsLiveData() {
    }

    override fun setupViewModel() {
        val id = getArgs().id
        viewModel = ViewModelProviders.of(
            this,
            requireActivity()
                .serviceLocator
                .getPostWithCommentsViewModelFactory(id)
        ).get(PostPageViewModel::class.java)
    }

    private fun getArgs() = requireContext()
        .serviceLocator
        .moshi
        .adapter(PostPageFragment.Args::class.java)
        .fromJson(arguments!!.getString(Tags.ARGS)!!)!!


}