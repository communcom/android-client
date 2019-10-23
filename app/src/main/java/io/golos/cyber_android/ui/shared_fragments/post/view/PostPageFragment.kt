package io.golos.cyber_android.ui.shared_fragments.post.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
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
import io.golos.cyber_android.ui.dialogs.ConfirmationDialog
import io.golos.cyber_android.ui.dialogs.PostPageMenuDialog
import io.golos.cyber_android.ui.dialogs.PostPageSortingComments
import io.golos.cyber_android.ui.screens.editor_page_activity.EditorPageActivity
import io.golos.cyber_android.ui.screens.profile.ProfileActivity
import io.golos.cyber_android.ui.shared_fragments.editor.view.EditorPageFragment
import io.golos.cyber_android.ui.shared_fragments.post.dto.SortingType
import io.golos.cyber_android.ui.shared_fragments.post.view.list.PostPageAdapter
import io.golos.cyber_android.ui.shared_fragments.post.view_commands.*
import io.golos.cyber_android.ui.shared_fragments.post.view_model.PostPageViewModel
import io.golos.domain.entities.CommentEntity
import io.golos.domain.interactors.model.CommentModel
import io.golos.domain.interactors.model.DiscussionIdModel
import io.golos.domain.interactors.model.PostModel
import io.golos.domain.post.post_dto.PostFormatVersion
import io.golos.domain.post.post_dto.PostType
import io.golos.domain.requestmodel.CommentFeedUpdateRequest
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.fragment_post.*
import javax.inject.Inject

/**
 * Fragment for single [PostModel] presentation
 */
class PostPageFragment : AbstractFeedFragment<CommentFeedUpdateRequest, CommentEntity, CommentModel, PostPageViewModel>() {

    private lateinit var binding: FragmentPostBinding

    private val POST_MENU_REQUEST = 102
    private val COMMENTS_SORTING_MENU_REQUEST = 103

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

        App.injections.get<PostPageFragmentComponent>(arguments!!.getParcelable<Args>(Tags.ARGS)!!.id).inject(this)

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeViewModel()

        postView.reduceDragSensitivity()

        postHeader.setOnBackButtonClickListener { activity?.finish() }
        postHeader.setOnMenuButtonClickListener { viewModel.onPostMenuClick() }
        postHeader.setOnUserClickListener { viewModel.onUserInHeaderClick(it) }
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

                is ShowCommentsSortingMenuViewCommand -> showCommentsSortingMenu()

                else -> throw UnsupportedOperationException("This command is not supported")
            }
        })
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode) {
            POST_MENU_REQUEST -> {
                when (resultCode) {
                    PostPageMenuDialog.RESULT_EDIT -> viewModel.editPost()
                    PostPageMenuDialog.RESULT_DELETE -> deletePost()
                }
            }
            COMMENTS_SORTING_MENU_REQUEST -> {
                when (resultCode) {
                    PostPageSortingComments.RESULT_INTERESTING_FIRST -> viewModel.updateCommentsSorting(SortingType.INTERESTING_FIRST)
                    PostPageSortingComments.RESULT_BY_TIME -> viewModel.updateCommentsSorting(SortingType.BY_TIME)
                }
            }
        }
    }

    override fun setupFeedAdapter() {
        val adapter = PostPageAdapter(viewModel, viewModel.commentsPageSize)
        adapter.setHasStableIds(true)
        feedList.adapter = adapter
    }

    private fun deletePost() {
        ConfirmationDialog.newInstance(getString(R.string.delete_post_confirmation)).run {
            listener = viewModel::deletePost
            show(this@PostPageFragment.requireFragmentManager(), "confirm")
        }
    }

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
            setTargetFragment(this@PostPageFragment, POST_MENU_REQUEST)
        }.show(requireFragmentManager(), "menu")
    }

    private fun showCommentsSortingMenu() {
        PostPageSortingComments.newInstance().apply {
            setTargetFragment(this@PostPageFragment, COMMENTS_SORTING_MENU_REQUEST)
        }.show(requireFragmentManager(), "menu")
    }
}