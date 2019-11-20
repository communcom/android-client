package io.golos.cyber_android.ui.shared_fragments.post.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import io.golos.cyber_android.R
import io.golos.cyber_android.application.App
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.post_page_fragment.PostPageFragmentComponent
import io.golos.cyber_android.databinding.FragmentPostBinding
import io.golos.cyber_android.ui.Tags
import io.golos.cyber_android.ui.common.ImageViewerActivity
import io.golos.cyber_android.ui.common.extensions.reduceDragSensitivity
import io.golos.cyber_android.ui.common.mvvm.FragmentBaseMVVM
import io.golos.cyber_android.ui.common.mvvm.view_commands.NavigateToMainScreenCommand
import io.golos.cyber_android.ui.common.mvvm.view_commands.ViewCommand
import io.golos.cyber_android.ui.dialogs.CommentsActionsDialog
import io.golos.cyber_android.ui.dialogs.ConfirmationDialog
import io.golos.cyber_android.ui.dialogs.post.PostPageMenuDialog
import io.golos.cyber_android.ui.dialogs.PostPageSortingComments
import io.golos.cyber_android.ui.screens.editor_page_activity.EditorPageActivity
import io.golos.cyber_android.ui.screens.profile.ProfileActivity
import io.golos.cyber_android.ui.shared_fragments.editor.view.EditorPageFragment
import io.golos.cyber_android.ui.shared_fragments.post.dto.SortingType
import io.golos.cyber_android.ui.shared_fragments.post.view.list.PostPageAdapter
import io.golos.cyber_android.ui.shared_fragments.post.view_commands.*
import io.golos.cyber_android.ui.shared_fragments.post.view_model.PostPageViewModel
import io.golos.domain.use_cases.model.DiscussionIdModel
import io.golos.domain.use_cases.model.PostModel
import io.golos.domain.use_cases.post.post_dto.PostFormatVersion
import io.golos.domain.use_cases.post.post_dto.PostType
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.fragment_post.*

/**
 * Fragment for single [PostModel] presentation
 */
class PostPageFragment : FragmentBaseMVVM<FragmentPostBinding, PostPageViewModel>() {
    @Parcelize
    data class Args(
        val id: DiscussionIdModel,
        val scrollToComments: Boolean = false
    ) : Parcelable

    override fun provideViewModelType(): Class<PostPageViewModel> = PostPageViewModel::class.java

    override fun layoutResId(): Int = R.layout.fragment_post

    override fun inject() =
        App.injections.get<PostPageFragmentComponent>(arguments!!.getParcelable<Args>(Tags.ARGS)!!.id).inject(this)

    override fun linkViewModel(binding: FragmentPostBinding, viewModel: PostPageViewModel) {
        binding.viewModel = viewModel
    }

    override fun releaseInjection() {
        App.injections.release<PostPageFragmentComponent>()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.post.observe(this, Observer {
            (postView.adapter as PostPageAdapter).update(it)
        })

        postView.reduceDragSensitivity()

        postHeader.setOnBackButtonClickListener { activity?.finish() }
        postHeader.setOnMenuButtonClickListener { viewModel.onPostMenuClick() }
        postHeader.setOnUserClickListener { viewModel.onUserInHeaderClick(it) }

        postComment.setOnSendClickListener { viewModel.onSendCommentClick(it) }

        postCommentEdit.setOnCloseClickListener { viewModel.cancelReplyOrEditComment() }
        postCommentEdit.setOnSendClickListener { viewModel.completeReplyOrEditComment(it) }

        // Setup the list
        val adapter = PostPageAdapter(viewModel, viewModel.commentsPageSize)
        adapter.setHasStableIds(true)
        postView.adapter = adapter

        val postListLayoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        postView.layoutManager = postListLayoutManager
        (postView.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
    }

    override fun onResume() {
        super.onResume()
        viewModel.setup()
    }

    override fun processViewCommand(command: ViewCommand) {
        when (command) {
            is NavigateToMainScreenCommand -> activity?.finish()

            is NavigateToImageViewCommand -> moveToImageView(command.imageUri)

            is NavigateToLinkViewCommand -> moveToLinkView(command.link)

            is NavigateToUserProfileViewCommand -> moveToUserProfile(command.userId)

            is StartEditPostViewCommand -> moveToEditPost(command.postId)

            is ShowPostMenuViewCommand -> showPostMenu(command.isMyPost, command.version, command.type)

            is ShowCommentsSortingMenuViewCommand -> showCommentsSortingMenu()

            is ClearCommentTextViewCommand -> postComment.clearText()

            is ShowCommentMenuViewCommand -> showCommentMenu(command.commentId)

            else -> throw UnsupportedOperationException("This command is not supported")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            PostPageMenuDialog.REQUEST -> {
                when (resultCode) {
                    PostPageMenuDialog.RESULT_EDIT -> viewModel.editPost()
                    PostPageMenuDialog.RESULT_DELETE -> deletePost()
                }
            }
            PostPageSortingComments.REQUEST -> {
                when (resultCode) {
                    PostPageSortingComments.RESULT_INTERESTING_FIRST -> viewModel.updateCommentsSorting(SortingType.INTERESTING_FIRST)
                    PostPageSortingComments.RESULT_BY_TIME -> viewModel.updateCommentsSorting(SortingType.BY_TIME)
                }
            }
            CommentsActionsDialog.REQUEST -> {
                when (resultCode) {
                    CommentsActionsDialog.RESULT_EDIT ->
                        viewModel.startEditComment(data!!.getParcelableExtra(CommentsActionsDialog.COMMENT_ID))
                    CommentsActionsDialog.RESULT_DELETE ->
                        viewModel.deleteComment(data!!.getParcelableExtra(CommentsActionsDialog.COMMENT_ID))
                }
            }
        }
    }

    private fun deletePost() {
        ConfirmationDialog.newInstance(getString(R.string.delete_post_confirmation)).run {
            listener = viewModel::deletePost
            show(this@PostPageFragment.requireFragmentManager(), "confirm")
        }
    }

    private fun moveToUserProfile(userId: String) = startActivity(ProfileActivity.getIntent(requireContext(), userId))

    private fun moveToImageView(imageUri: Uri) =
        startActivity(ImageViewerActivity.getIntent(requireContext(), imageUri.toString()))

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
            setTargetFragment(this@PostPageFragment, PostPageMenuDialog.REQUEST)
        }.show(requireFragmentManager(), "menu")
    }

    private fun showCommentsSortingMenu() {
        PostPageSortingComments.newInstance().apply {
            setTargetFragment(this@PostPageFragment, PostPageSortingComments.REQUEST)
        }.show(requireFragmentManager(), "menu")
    }

    private fun showCommentMenu(commentId: DiscussionIdModel) {
        CommentsActionsDialog.newInstance(commentId).apply {
            setTargetFragment(this@PostPageFragment, CommentsActionsDialog.REQUEST)
        }.show(requireFragmentManager(), "menu")
    }
}