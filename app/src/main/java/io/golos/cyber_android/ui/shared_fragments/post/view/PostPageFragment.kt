package io.golos.cyber_android.ui.shared_fragments.post.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.golos.cyber_android.R
import io.golos.cyber_android.application.App
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.post_page_fragment.PostPageFragmentComponent
import io.golos.cyber_android.databinding.FragmentPostBinding
import io.golos.cyber_android.ui.Tags
import io.golos.cyber_android.ui.common.ImageViewerActivity
import io.golos.cyber_android.ui.common.extensions.reduceDragSensitivity
import io.golos.cyber_android.ui.common.mvvm.viewModel.FragmentViewModelFactory
import io.golos.cyber_android.ui.common.mvvm.view_commands.NavigateToMainScreenCommand
import io.golos.cyber_android.ui.common.mvvm.view_commands.SetLoadingVisibilityCommand
import io.golos.cyber_android.ui.common.mvvm.view_commands.ShowMessageCommand
import io.golos.cyber_android.ui.common.posts.AbstractFeedFragment
import io.golos.cyber_android.ui.common.utils.ViewUtils
import io.golos.cyber_android.ui.common.widgets.CommentWidget
import io.golos.cyber_android.ui.dialogs.ConfirmationDialog
import io.golos.cyber_android.ui.dialogs.PostPageMenuDialog
import io.golos.cyber_android.ui.screens.editor_page_activity.EditorPageActivity
import io.golos.cyber_android.ui.screens.profile.ProfileActivity
import io.golos.cyber_android.ui.shared_fragments.editor.view.EditorPageFragment
import io.golos.cyber_android.ui.shared_fragments.post.view.list.PostPageAdapter
import io.golos.cyber_android.ui.shared_fragments.post.view_commands.*
import io.golos.cyber_android.ui.shared_fragments.post.view_model.PostPageViewModel
import io.golos.domain.entities.CommentEntity
import io.golos.domain.interactors.model.*
import io.golos.domain.post.post_dto.PostFormatVersion
import io.golos.domain.post.post_dto.PostType
import io.golos.domain.requestmodel.CommentFeedUpdateRequest
import io.golos.domain.requestmodel.QueryResult
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.fragment_post.*
import javax.inject.Inject


const val INPUT_ANIM_DURATION = 400L

const val GALLERY_REQUEST = 101
const val POST_MENU_REQUEST = 102

/**
 * Fragment for single [PostModel] presentation
 */
class PostPageFragment : AbstractFeedFragment<CommentFeedUpdateRequest, CommentEntity, CommentModel, PostPageViewModel>() {

    private lateinit var binding: FragmentPostBinding

    @Parcelize
    data class Args(
        val id: DiscussionIdModel,
        val scrollToComments: Boolean = false
    ): Parcelable

    override val feedList: RecyclerView
        get() = postView

    @Inject
    internal lateinit var viewModelFactory: FragmentViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        App.injections.get<PostPageFragmentComponent>(getArgs().id).inject(this)

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(PostPageViewModel::class.java)
    }

    override fun onDestroy() {
        super.onDestroy()
        App.injections.release<PostPageFragmentComponent>()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_post, container, false)
        binding.lifecycleOwner = this

        binding.viewModel = viewModel
        return binding.root

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setupCommentWidget()
        observeViewModel()

        postView.reduceDragSensitivity()

        postHeader.setOnBackButtonClickListener { activity?.finish() }

        postHeader.setOnMenuButtonClickListener { viewModel.onPostMenuClick() }

        postHeader.setOnUserClickListener { viewModel.onUserInHeaderClick(it) }

        feedList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                adjustInputVisibility((feedList.layoutManager as LinearLayoutManager).findLastVisibleItemPosition())
            }
        })
    }

    override fun onResume() {
        super.onResume()
        viewModel.setup()
    }

    private fun observeViewModel() {
        viewModel.post.observe(this, Observer {
            (feedList.adapter as PostPageAdapter).update(it)
        })

        viewModel.command.observe(this, Observer { command ->
            when(command) {
                is SetLoadingVisibilityCommand -> setLoadingVisibility(command.isVisible)

                is ShowMessageCommand -> uiHelper.showMessage(command.textResId)

                is NavigateToMainScreenCommand -> activity?.finish()

                is NavigateToImageViewCommand -> moveToImageView(command.imageUri)

                is NavigateToLinkViewCommand -> moveToLinkView(command.link)

                is NavigateToUserProfileViewCommand -> moveToUserProfile(command.userId)

                is StartEditPostViewCommand -> moveToEditPost(command.postId)

                is ShowPostMenuViewCommand -> showPostMenu(command.isMyPost, command.version, command.type)

                else -> throw UnsupportedOperationException("This command is not supported")
            }
        })

//        viewModel.loadingStatusLiveData.observe(this, Observer {
//            showFeedLoading()
//        })


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
                        showDiscussionCreationError(result.error)
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

//        viewModel.isMyPostLiveData.observe(this, Observer { isMyPost ->
//            postMenu.visibility = if (isMyPost) View.VISIBLE else View.GONE
//
//            postMenu.setOnClickListener {
//                val postMetadata = viewModel.post.value!!.content.body.postBlock.metadata
//
//                PostPageMenuDialog.newInstance(isMyPost, postMetadata.type, postMetadata.version).apply {
//                    setTargetFragment(this@PostPageFragment,
//                        POST_MENU_REQUEST
//                    )
//                }.show(requireFragmentManager(), "menu")
//            }
//        })
    }

    private var scrolled = false

    private fun scrollToCommentsIfNeeded() {
/*
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
*/
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

        postCommentBottom.visibility = View.GONE
        postCommentBottom.alpha = 0f
    }

    private fun addCommentByParent(discussionId: DiscussionIdModel) {
        viewModel.setDiscussionToReply(discussionId)
    }

    private fun adjustInputVisibility(lastVisibleItem: Int) {
/*
        if (lastVisibleItem >= (feedList.adapter as PostPageAdapter).getCommentsTitlePosition()) {
            viewModel.setCommentInputVisibility(PostPageViewModel.Visibility.VISIBLE)
        } else {
            viewModel.setCommentInputVisibility(PostPageViewModel.Visibility.GONE)
        }
*/
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

    private fun editPost() = viewModel.editPost()

    private fun deletePost() {
        ConfirmationDialog.newInstance(getString(R.string.delete_post_confirmation)).run {
            listener = viewModel::deletePost
            show(this@PostPageFragment.requireFragmentManager(), "confirm")
        }
    }

//    private fun showFeedLoading() {
//
//    }

//    private fun bindPostModel(postModel: PostModel) {
//        (feedList.adapter as PostPageAdapter).postModel = postModel
//        bindToolbar(postModel)
//    }

//    private fun bindToolbar(postModel: PostModel) {
//        if (postModel.author.avatarUrl.isNotBlank()) {
//            Glide.with(requireContext())
//                .load(postModel.author.avatarUrl)
//                .apply(RequestOptions.circleCropTransform())
//                .into(postAvatar)
//            postAvatarName.text = ""
//        }
//        else {
//            Glide.with(requireContext())
//                .load(0)
//                .into(postAvatar)
//            postAvatarName.text = postModel.author.username
//        }
//
//        postAuthorName.text = postModel.community.name
//        postAuthor.text = String.format(
//            resources.getString(R.string.post_time_and_author_format),
//            DateUtils.createTimeLabel(
//                postModel.meta.time.time,
//                postModel.meta.elapsedFormCreation.elapsedMinutes,
//                postModel.meta.elapsedFormCreation.elapsedHours,
//                postModel.meta.elapsedFormCreation.elapsedDays,
//                requireContext()
//            ),
//            postModel.author.username
//        )
//
//        postHeaderLayout.setOnClickListener {
//            startActivity(ProfileActivity.getIntent(requireContext(), postModel.author.userId.userId))
//        }
//    }

    override fun setupEventsProvider() {
    }

    override fun setupFeedAdapter() {
        feedList.adapter = PostPageAdapter(viewModel)

/*
        feedList.adapter =
            PostPageAdapter(
                viewLifecycleOwner,
                object : CommentsAdapter.Listener {
                    override fun onUsernameClick(userId: String) {
                        startActivity(ProfileActivity.getIntent(requireContext(), userId))
                    }

                    override fun onImageLinkClick(url: String) {
//                        startActivity(ImageViewerActivity.getIntent(requireContext(), url))
                    }

                    override fun onWebLinkClick(url: String) {
//                        val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
//                        if (webIntent.resolveActivity(requireActivity().packageManager) != null) {
//                            startActivity(webIntent)
//                        }
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
                viewModel
            )
*/
    }

    override fun onNewData(data: List<CommentModel>) {
    }

    override fun setupWidgetsLiveData() {
    }

    private fun getArgs() = arguments!!.getParcelable<Args>(Tags.ARGS)

    private fun moveToUserProfile(userId: String) = startActivity(ProfileActivity.getIntent(requireContext(), userId))

    private fun moveToImageView(imageUri: Uri) = startActivity(ImageViewerActivity.getIntent(requireContext(), imageUri.toString()))

    private fun moveToLinkView(link: Uri) {
        Intent(Intent.ACTION_VIEW, link)
            .also { intent ->
                if (intent.resolveActivity(requireActivity().packageManager) != null) {
                    startActivity(intent)
                }
            }
    }

    private fun moveToEditPost(postId: DiscussionIdModel) =
        startActivity(EditorPageActivity.getIntent(requireContext(), EditorPageFragment.Args(postId)))

    private fun showPostMenu(isMyPost: Boolean, version: PostFormatVersion, type: PostType) {
        PostPageMenuDialog.newInstance(isMyPost, type, version).apply {
            setTargetFragment(this@PostPageFragment,
                POST_MENU_REQUEST
            )
        }.show(requireFragmentManager(), "menu")
    }
}