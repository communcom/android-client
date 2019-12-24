package io.golos.cyber_android.ui.screens.post_page_menu.view

import android.app.Activity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.Tags
import io.golos.cyber_android.ui.common.recycler_view.versioned.VersionedListItem
import io.golos.cyber_android.ui.dialogs.base.BottomSheetDialogFragmentBase
import io.golos.cyber_android.ui.screens.post_page_menu.model.*
import io.golos.cyber_android.ui.screens.post_page_menu.view.list.PostMenuAdapter
import io.golos.cyber_android.ui.shared_fragments.post.dto.PostHeader
import io.golos.domain.use_cases.post.post_dto.DocumentFormatVersion
import io.golos.domain.use_cases.post.post_dto.PostType
import kotlinx.android.synthetic.main.dialog_post_menu.*

/**
 * [BottomSheetDialogFragment] that shows post menu
 */
class PostPageMenuDialog : BottomSheetDialogFragmentBase(), PostMenuModelListEventProcessor {
    companion object {
        const val REQUEST = 9303

        const val RESULT_ADD_FAVORITE = Activity.RESULT_FIRST_USER + 1
        const val RESULT_REMOVE_FAVORITE = Activity.RESULT_FIRST_USER + 2
        const val RESULT_SHARE = Activity.RESULT_FIRST_USER + 3
        const val RESULT_EDIT = Activity.RESULT_FIRST_USER + 4
        const val RESULT_DELETE = Activity.RESULT_FIRST_USER + 5
        const val RESULT_SUBSCRIBE = Activity.RESULT_FIRST_USER + 6
        const val RESULT_UNSUBSCRIBE = Activity.RESULT_FIRST_USER + 7
        const val RESULT_REPORT = Activity.RESULT_FIRST_USER + 8

        private const val POST_MENU = "POST_MENU"

        private lateinit var postMenu: PostMenu

        @Deprecated("Need use another method newInstance")
        fun newInstance(
            isMyPost: Boolean,
            type: PostType,
            formatVersion: DocumentFormatVersion,
            payload: String = ""
        ): PostPageMenuDialog {
            return PostPageMenuDialog().apply {
                arguments = Bundle().apply {
                    putSerializable("", isMyPost)
                    putString("", payload)
                    putInt("", type.value)
                    putParcelable("", formatVersion)
                }
            }
        }

        fun newInstance(
            postMenu: PostMenu
        ): PostPageMenuDialog {
            return PostPageMenuDialog().apply {
                arguments = Bundle().apply {
                    putParcelable(POST_MENU, postMenu)
                }
            }
        }
    }

    override fun onAddToFavoriteItemClick() {
        setSelectAction(RESULT_ADD_FAVORITE){
            putExtra(Tags.POST_MENU, postMenu)
        }
    }

    override fun onRemoveFromFavoriteItemClick() {
        setSelectAction(RESULT_REMOVE_FAVORITE){
            putExtra(Tags.POST_MENU, postMenu)
        }
    }

    override fun onShareItemClick() {
        setSelectAction(RESULT_SHARE) {
            putExtra(Tags.POST_MENU, postMenu)
        }
    }

    override fun onEditItemClick() {
        setSelectAction(RESULT_EDIT){
            putExtra(Tags.POST_MENU, postMenu)
        }
    }

    override fun onDeleteItemClick() {
        setSelectAction(RESULT_DELETE){
            putExtra(Tags.POST_MENU, postMenu)
        }
    }

    override fun onJoinItemClick() {
        setSelectAction(RESULT_SUBSCRIBE) {
            putExtra(Tags.POST_MENU, postMenu)
        }
    }

    override fun onJoinedItemClick() {
        setSelectAction(RESULT_UNSUBSCRIBE) {
            putExtra(Tags.POST_MENU, postMenu)
        }
    }

    override fun onReportItemClick() {
        setSelectAction(RESULT_REPORT){
            putExtra(Tags.POST_MENU, postMenu)
        }
    }

    override fun provideLayout(): Int = R.layout.dialog_post_menu

    override fun setupView() {
        postMenu = arguments!!.getParcelable(POST_MENU)

        postMenu?.let { menu ->
            val adapter = PostMenuAdapter(this)
            rvMenu.layoutManager = LinearLayoutManager(requireContext())
            rvMenu.adapter = adapter

            postMenuHeader.setHeader(
                PostHeader(
                    communityName = menu.communityName,
                    communityAvatarUrl = menu.communityAvatarUrl,
                    actionDateTime = menu.creationTime,
                    userName = menu.authorUsername,
                    userId = menu.authorUserId,
                    canJoinToCommunity = false,
                    isBackFeatureEnabled = false,
                    isJoinFeatureEnabled = false
                )
            )
            postMenuHeader.hideActionMenu()

            val isMyPost = menu.isMyPost
            val listOfItems =
                if (isMyPost) generateMyPostMenu(menu)
                else generateNotMyPostMenu(menu)
            adapter.update(listOfItems)
        }

        ivClose.setOnClickListener { dismiss() }
    }

    private fun generateMyPostMenu(postMenu: PostMenu): List<VersionedListItem> {
        val items = mutableListOf<VersionedListItem>()

        postMenu.shareUrl?.let { shareUrl ->
            items.add(ShareListItem(shareUrl))
        }
        items.add(EditListItem())
        items.add(DeleteListItem())

        return items
    }

    private fun generateNotMyPostMenu(postMenu: PostMenu): List<VersionedListItem> {
        val items = mutableListOf<VersionedListItem>()

        if (postMenu.isSubscribed) {
            items.add(JoinedListItem())
        } else {
            items.add(JoinListItem())
        }
        items.add(ReportListItem())

        return items
    }
}