package io.golos.cyber_android.ui.screens.profile_comments.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.golos.cyber_android.R
import io.golos.cyber_android.application.App
import io.golos.cyber_android.databinding.FragmentProfileCommentsBinding
import io.golos.cyber_android.ui.dto.Comment
import io.golos.cyber_android.ui.dto.ProfileItem
import io.golos.cyber_android.ui.mappers.mapToCommentMenu
import io.golos.cyber_android.ui.screens.comment_page_menu.model.CommentMenu
import io.golos.cyber_android.ui.screens.comment_page_menu.view.CommentPageMenuDialog
import io.golos.cyber_android.ui.screens.community_page.view.CommunityPageFragment
import io.golos.cyber_android.ui.screens.profile_comments.di.ProfileCommentsFragmentComponent
import io.golos.cyber_android.ui.screens.profile_comments.model.item.ProfileCommentListItem
import io.golos.cyber_android.ui.screens.profile_comments.view.list.ProfileCommentsAdapter
import io.golos.cyber_android.ui.screens.profile_comments.view.view_commands.NavigateToEditComment
import io.golos.cyber_android.ui.screens.profile_comments.view_model.ProfileCommentsViewModel
import io.golos.cyber_android.ui.screens.profile_photos.view.ProfilePhotosFragment
import io.golos.cyber_android.ui.shared.Tags
import io.golos.cyber_android.ui.shared.mvvm.FragmentBaseMVVM
import io.golos.cyber_android.ui.shared.mvvm.view_commands.*
import io.golos.cyber_android.ui.shared.paginator.Paginator
import io.golos.cyber_android.ui.shared.utils.openImageView
import io.golos.cyber_android.ui.shared.utils.openLinkView
import io.golos.domain.dto.UserIdDomain
import kotlinx.android.synthetic.main.fragment_profile_comments.*

class ProfileCommentsFragment : FragmentBaseMVVM<FragmentProfileCommentsBinding, ProfileCommentsViewModel>() {

    private var collapseListener: (() -> Unit)? = null

    override fun provideViewModelType(): Class<ProfileCommentsViewModel> = ProfileCommentsViewModel::class.java

    override fun layoutResId(): Int = R.layout.fragment_profile_comments

    override fun inject() =
        App.injections.get<ProfileCommentsFragmentComponent>(
            arguments!!.getParcelable<UserIdDomain>(USER_ID_EXTRA)
        ).inject(this)

    override fun releaseInjection() {
        App.injections.release<ProfileCommentsFragmentComponent>()
    }

    override fun linkViewModel(binding: FragmentProfileCommentsBinding, viewModel: ProfileCommentsViewModel) {
        binding.viewModel = viewModel
    }

    fun setCollapseListener(listener: () -> Unit) {
        collapseListener = listener
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupCommentsList()
        observeViewModel()

        btnRetry.setOnClickListener {
            viewModel.onRetryLoadComments()
        }
        commentWidget.setOnSendClickListener { comment ->
            viewModel.onSendComment(comment)
            commentWidget.visibility = View.GONE
        }
        commentWidget.onAttachImageListener = {
            openSelectPhotoView(it)
        }
    }

    private fun setupCommentsList() {
        val communityAdapter = ProfileCommentsAdapter(viewModel)
        val lManager = LinearLayoutManager(requireContext())
        rvComments.layoutManager = lManager
        rvComments.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val visibleItemCount = recyclerView.childCount
                val totalItemCount = lManager.itemCount
                val firstVisibleItem = lManager.findFirstVisibleItemPosition()

                if (totalItemCount - visibleItemCount <= firstVisibleItem + visibleItemCount) {
                    if (lManager.findLastCompletelyVisibleItemPosition() >= totalItemCount - 1) {
                        viewModel.loadMoreComments()
                    }
                }
            }
        })

        rvComments.adapter = communityAdapter
    }

    override fun processViewCommand(command: ViewCommand) {
        when (command) {
            is NavigateToImageViewCommand -> requireContext().openImageView(command.imageUri)

            is NavigateToLinkViewCommand -> requireContext().openLinkView(command.link)

            is NavigateToUserProfileViewCommand -> openSelectPhotoView(command.userId)

            is NavigateToCommunityPageCommand -> openCommunityPage(command.communityId)

            is NavigateToProfileCommentMenuDialogViewCommand -> openProfileCommentMenu(command.comment)

            is NavigateToEditComment -> {
                commentWidget.setCommentForEdit(command.comment)
                commentWidget.visibility = View.VISIBLE
                collapseListener?.invoke()
            }
        }
    }

    private fun openSelectPhotoView(imageUrl: String?) {
        getDashboardFragment(this)
            ?.showFragment(
                ProfilePhotosFragment.newInstance(
                    ProfileItem.COMMENT,
                    imageUrl,
                    this@ProfileCommentsFragment
                )
            )
    }

    private fun openCommunityPage(communityId: String) {
        getDashboardFragment(this)?.showFragment(
            CommunityPageFragment.newInstance(communityId)
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            CommentPageMenuDialog.REQUEST -> {
                when (resultCode) {
                    CommentPageMenuDialog.RESULT_EDIT -> {
                        val commentMenu: CommentMenu? = data?.extras?.getParcelable(Tags.COMMENT_MENU)
                        commentMenu?.let { comment ->
                            comment.contentId?.let {
                                viewModel.editComment(it)
                            }
                        }
                    }
                    CommentPageMenuDialog.RESULT_DELETE -> {
                        val commentMenu: CommentMenu? = data?.extras?.getParcelable(Tags.COMMENT_MENU)
                        commentMenu?.let { comment ->
                            viewModel.deleteComment(
                                comment.permlink,
                                comment.communityId
                            )
                        }
                    }
                }
            }
            ProfilePhotosFragment.REQUEST -> {
                val result = data?.extras?.getParcelable<ProfilePhotosFragment.Result>(ProfilePhotosFragment.RESULT)
                commentWidget.updateImageAttachment(result?.photoFilePath)
            }
        }
    }


    private fun openProfileCommentMenu(comment: Comment) {
        CommentPageMenuDialog.newInstance(
            comment.mapToCommentMenu()
        ).apply {
            setTargetFragment(this@ProfileCommentsFragment, CommentPageMenuDialog.REQUEST)
        }.show(requireFragmentManager(), "show")
    }

    private fun observeViewModel() {
        viewModel.commentListState.observe(viewLifecycleOwner, Observer { state ->
            val cAdapter = rvComments.adapter as ProfileCommentsAdapter
            when (state) {
                is Paginator.State.Data<*> -> {
                    cAdapter.removeProgress()
                    cAdapter.removeRetry()
                    val items = (state.data as MutableList<ProfileCommentListItem>)
                    cAdapter.update(items)
                    btnRetry.visibility = View.INVISIBLE
                    emptyProgressLoading.visibility = View.INVISIBLE
                }
                is Paginator.State.FullData<*> -> {
                    cAdapter.removeProgress()
                    cAdapter.removeRetry()
                    val items = (state.data as MutableList<ProfileCommentListItem>)
                    cAdapter.update(items)
                    btnRetry.visibility = View.INVISIBLE
                    emptyProgressLoading.visibility = View.INVISIBLE
                }
                is Paginator.State.PageError<*> -> {
                    cAdapter.removeProgress()
                    cAdapter.addRetry()
                    rvComments.scrollToPosition(cAdapter.itemCount - 1)
                }
                is Paginator.State.NewPageProgress<*> -> {
                    cAdapter.removeRetry()
                    cAdapter.addProgress()
                    rvComments.scrollToPosition(cAdapter.itemCount - 1)
                }
                is Paginator.State.EmptyProgress -> {
                    cAdapter.removeProgress()
                    cAdapter.removeRetry()
                    btnRetry.visibility = View.INVISIBLE
                    emptyProgressLoading.visibility = View.VISIBLE
                }
                is Paginator.State.Empty -> {
                    cAdapter.update(mutableListOf())
                    cAdapter.removeProgress()
                    cAdapter.removeRetry()
                    btnRetry.visibility = View.INVISIBLE
                    emptyProgressLoading.visibility = View.INVISIBLE
                }
                is Paginator.State.EmptyError -> {
                    cAdapter.removeProgress()
                    cAdapter.removeRetry()
                    btnRetry.visibility = View.VISIBLE
                    emptyProgressLoading.visibility = View.INVISIBLE
                }
            }
        })
    }

    companion object {

        private const val USER_ID_EXTRA = "user_id"

        fun newInstance(
            userId: UserIdDomain
        ) = ProfileCommentsFragment().apply {
            arguments = Bundle().apply {
                putParcelable(USER_ID_EXTRA, userId)
            }
        }
    }
}