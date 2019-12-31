package io.golos.cyber_android.ui.screens.comment_page_menu.view

import android.app.Activity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.Tags
import io.golos.cyber_android.ui.common.recycler_view.versioned.VersionedListItem
import io.golos.cyber_android.ui.dialogs.base.BottomSheetDialogFragmentBase
import io.golos.cyber_android.ui.screens.comment_page_menu.model.CommentMenu
import io.golos.cyber_android.ui.screens.comment_page_menu.model.CommentMenuModelListEventProcessor
import io.golos.cyber_android.ui.screens.comment_page_menu.model.item.CommentDeleteListItem
import io.golos.cyber_android.ui.screens.comment_page_menu.model.item.CommentEditListItem
import io.golos.cyber_android.ui.screens.comment_page_menu.view.list.CommentMenuAdapter
import io.golos.cyber_android.ui.screens.post_view.dto.PostHeader
import kotlinx.android.synthetic.main.dialog_profile_comment_menu.*

class CommentPageMenuDialog : BottomSheetDialogFragmentBase(), CommentMenuModelListEventProcessor {

    override fun provideLayout(): Int = R.layout.dialog_profile_comment_menu

    override fun setupView() {
        commentMenu = arguments?.getParcelable(COMMENT_MENU)!!

        val adapter = CommentMenuAdapter(this)
        rvMenu.layoutManager = LinearLayoutManager(requireContext())
        rvMenu.adapter = adapter

        commentMenuHeader.setHeader(
            PostHeader(
                communityName = commentMenu.communityName,
                communityAvatarUrl = commentMenu.communityAvatarUrl,
                actionDateTime = commentMenu.creationTime,
                userName = commentMenu.authorUsername,
                userId = commentMenu.authorUserId,
                canJoinToCommunity = false,
                isBackFeatureEnabled = false,
                isJoinFeatureEnabled = false
            )
        )
        commentMenuHeader.hideActionMenu()

        val items = mutableListOf<VersionedListItem>()
        with(items) {
            add(CommentEditListItem())
            add(CommentDeleteListItem())
        }
        adapter.update(items)

        ivClose.setOnClickListener { dismiss() }
    }

    override fun onEditCommentEvent() {
        setSelectAction(RESULT_EDIT) {
            putExtra(Tags.COMMENT_MENU, commentMenu)
        }
    }

    override fun onDeleteCommentEvent() {
        setSelectAction(RESULT_DELETE) {
            putExtra(Tags.COMMENT_MENU, commentMenu)
        }
    }

    companion object {
        const val REQUEST = 98377

        const val RESULT_EDIT = Activity.RESULT_FIRST_USER + 1
        const val RESULT_DELETE = Activity.RESULT_FIRST_USER + 2

        private const val COMMENT_MENU = "COMMENT_MENU"

        private lateinit var commentMenu: CommentMenu

        fun newInstance(
            commentMenu: CommentMenu
        ): CommentPageMenuDialog {
            return CommentPageMenuDialog().apply {
                arguments = Bundle().apply {
                    putParcelable(COMMENT_MENU, commentMenu)
                }
            }
        }
    }
}