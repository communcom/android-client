package io.golos.cyber_android.ui.screens.comment_page_menu.view

import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.dialogs.base.BottomSheetDialogFragmentBase
import io.golos.cyber_android.ui.screens.comment_page_menu.model.CommentMenu
import io.golos.cyber_android.ui.screens.comment_page_menu.model.CommentMenuModelListEventProcessor
import io.golos.cyber_android.ui.screens.comment_page_menu.model.item.CommentDeleteListItem
import io.golos.cyber_android.ui.screens.comment_page_menu.model.item.CommentEditListItem
import io.golos.cyber_android.ui.screens.comment_page_menu.view.list.CommentMenuAdapter
import io.golos.cyber_android.ui.screens.post_view.dto.PostHeader
import io.golos.cyber_android.ui.shared.recycler_view.versioned.VersionedListItem
import kotlinx.android.synthetic.main.dialog_profile_comment_menu.*

class CommentPageMenuDialog(
    private val commentMenu: CommentMenu
) : BottomSheetDialogFragmentBase<CommentPageMenuDialog.Result>(),
    CommentMenuModelListEventProcessor {

    sealed class Result {
        data class Edit (val commentMenu: CommentMenu): Result()
        data class Delete (val commentMenu: CommentMenu): Result()
    }

    companion object {
        fun show(parent: Fragment, commentMenu: CommentMenu, closeAction: (Result?) -> Unit) =
            CommentPageMenuDialog(commentMenu)
                .apply { closeActionListener = closeAction }
                .show(parent.parentFragmentManager, "COMMENT_PAGE_MENU_DIALOG")
    }

    override val closeButton: View?
        get() = ivClose

    override val layout: Int
        get() = R.layout.dialog_profile_comment_menu

    override fun setupView() {
        val adapter = CommentMenuAdapter(this)
        rvMenu.layoutManager = LinearLayoutManager(requireContext())
        rvMenu.adapter = adapter

        commentMenuHeader.setHeader(
            PostHeader(
                communityName = commentMenu.communityName,
                communityAvatarUrl = commentMenu.communityAvatarUrl,
                communityId = commentMenu.communityId,
                actionDateTime = commentMenu.creationTime,
                userName = commentMenu.authorUsername,
                userId = commentMenu.authorUserId,
                userAvatarUrl = commentMenu.authorAvatarUrl,
                canJoinToCommunity = false,
                isBackFeatureEnabled = false,
                reward = null
            )
        )
        commentMenuHeader.hideActionMenu()

        val items = mutableListOf<VersionedListItem>()
        with(items) {
            add(CommentEditListItem())
            add(CommentDeleteListItem())
        }
        adapter.update(items)
    }

    override fun onEditCommentEvent() = closeOnItemSelected(Result.Edit(commentMenu))

    override fun onDeleteCommentEvent() = closeOnItemSelected(Result.Delete(commentMenu))
}