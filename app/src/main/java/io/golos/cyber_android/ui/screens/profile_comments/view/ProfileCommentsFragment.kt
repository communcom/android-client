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
import io.golos.cyber_android.ui.dialogs.donation.DonationUsersDialog
import io.golos.cyber_android.ui.dto.Comment
import io.golos.cyber_android.ui.dto.ProfileItem
import io.golos.cyber_android.ui.mappers.mapToCommentMenu
import io.golos.cyber_android.ui.screens.comment_page_menu.view.CommentPageMenuDialog
import io.golos.cyber_android.ui.screens.community_page.view.CommunityPageFragment
import io.golos.cyber_android.ui.screens.dashboard.view.DashboardFragment
import io.golos.cyber_android.ui.screens.donate_send_points.view.DonateSendPointsFragment
import io.golos.cyber_android.ui.screens.post_view.view.PostPageFragment
import io.golos.cyber_android.ui.screens.profile.view.ProfileExternalUserFragment
import io.golos.cyber_android.ui.screens.profile.view.ProfileFragment
import io.golos.cyber_android.ui.screens.profile_comments.di.ProfileCommentsFragmentComponent
import io.golos.cyber_android.ui.screens.profile_comments.model.item.ProfileCommentListItem
import io.golos.cyber_android.ui.screens.profile_comments.view.list.ProfileCommentsAdapter
import io.golos.cyber_android.ui.screens.profile_comments.view.view_commands.NavigateToEditComment
import io.golos.cyber_android.ui.screens.profile_comments.view_model.ProfileCommentsViewModel
import io.golos.cyber_android.ui.screens.profile_photos.view.ProfilePhotosFragment
import io.golos.cyber_android.ui.shared.mvvm.FragmentBaseMVVM
import io.golos.cyber_android.ui.shared.mvvm.view_commands.*
import io.golos.cyber_android.ui.shared.paginator.Paginator
import io.golos.cyber_android.ui.shared.utils.openImageView
import io.golos.cyber_android.ui.shared.utils.openLinkView
import io.golos.domain.dto.CommunityIdDomain
import io.golos.domain.dto.ContentIdDomain
import io.golos.domain.dto.DonationsDomain
import io.golos.domain.dto.UserIdDomain
import io.golos.domain.use_cases.model.DiscussionIdModel
import kotlinx.android.synthetic.main.fragment_profile_comments.*

class ProfileCommentsFragment : FragmentBaseMVVM<FragmentProfileCommentsBinding, ProfileCommentsViewModel>() {
    companion object {
        private const val USER_ID_EXTRA = "user_id"

        fun newInstance(userId: UserIdDomain) = ProfileCommentsFragment().apply {
            arguments = Bundle().apply {
                putParcelable(USER_ID_EXTRA, userId)
            }
        }
    }

    private var collapseListener: (() -> Unit)? = null

    override fun provideViewModelType(): Class<ProfileCommentsViewModel> = ProfileCommentsViewModel::class.java

    override fun layoutResId(): Int = R.layout.fragment_profile_comments

    override fun inject(key: String) =
        App.injections.get<ProfileCommentsFragmentComponent>(
            key,
            arguments!!.getParcelable<UserIdDomain>(USER_ID_EXTRA)
        ).inject(this)

    override fun releaseInjection(key: String) = App.injections.release<ProfileCommentsFragmentComponent>(key)

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
        commentWidget.onSendClickListener = { comment ->
            viewModel.onSendComment(comment)
            commentWidget.visibility = View.GONE
        }
        commentWidget.onAttachImageListener = {
            openSelectPhotoView(it)
        }
        commentWidget.onClearClickListener = {
            commentWidget.visibility = View.GONE
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
            is NavigateToUserProfileCommand -> navigateToUser(command.userId)
            is NavigateToPostCommand -> openPost(command.discussionIdModel, command.contentId)
            is ScrollProfileToTopCommand ->  scrollProfileToTop()
            is NavigateToCommunityPageCommand -> openCommunityPage(command.communityId)
            is NavigateToProfileCommentMenuDialogViewCommand -> openProfileCommentMenu(command.comment)
            is NavigateToEditComment -> {
                commentWidget.setCommentForEdit(command.comment.contentId, command.comment.body)
                commentWidget.visibility = View.VISIBLE
                collapseListener?.invoke()
            }
            is NavigateToDonateCommand -> moveToDonate(command)
            is ShowDonationUsersDialogCommand -> showDonationUsersDialogCommand(command.donation)
        }
    }

    private fun openPost(discussionIdModel: DiscussionIdModel, contentId: ContentIdDomain) {
        getDashboardFragment(this)?.navigateToFragment(PostPageFragment.newInstance(PostPageFragment.Args(discussionIdModel, contentId)), tag = contentId.permlink)
    }

    private fun scrollProfileToTop() {
        when(parentFragment) {
            is DashboardFragment -> (parentFragment as DashboardFragment).scrollProfileToTop()
            is ProfileExternalUserFragment -> (parentFragment as ProfileFragment).scrollToTop()
        }
    }

    private fun openSelectPhotoView(imageUrl: String?) {
        getDashboardFragment(this)
            ?.navigateToFragment(
                ProfilePhotosFragment.newInstance(
                    ProfileItem.COMMENT,
                    imageUrl,
                    this@ProfileCommentsFragment
                )
            )
    }

    private fun navigateToUser(userId: UserIdDomain) {
        getDashboardFragment(this)?.navigateToFragment(ProfileExternalUserFragment.newInstance(userId))
    }

    private fun openCommunityPage(communityId: CommunityIdDomain) {
        getDashboardFragment(this)?.navigateToFragment(
            CommunityPageFragment.newInstance(communityId)
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            ProfilePhotosFragment.REQUEST -> {
                val result = data?.extras?.getParcelable<ProfilePhotosFragment.Result>(ProfilePhotosFragment.RESULT)
                commentWidget.updateImageAttachment(result?.photoFilePath)
            }
        }
    }


    private fun openProfileCommentMenu(comment: Comment) =
        CommentPageMenuDialog.show(this@ProfileCommentsFragment, comment.mapToCommentMenu()) {
            when(it) {
                is CommentPageMenuDialog.Result.Edit -> it.commentMenu.contentId?.let { viewModel.editComment(it) }
                is CommentPageMenuDialog.Result.Delete -> viewModel.deleteComment(it.commentMenu.permlink, it.commentMenu.communityId)
            }
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

    private fun moveToDonate(command: NavigateToDonateCommand) =
        getDashboardFragment(this)?.navigateToFragment(
            DonateSendPointsFragment.newInstance(
                command.contentId,
                command.communityId,
                command.contentAuthor,
                command.balance,
                command.amount))

    private fun showDonationUsersDialogCommand(donations: DonationsDomain) = DonationUsersDialog.show(this, donations) {
        (it as? DonationUsersDialog.Result.ItemSelected)?.user?.let { viewModel.onUserClicked(it.userId) }
    }
}