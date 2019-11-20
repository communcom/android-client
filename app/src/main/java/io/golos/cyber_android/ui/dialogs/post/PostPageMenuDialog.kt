package io.golos.cyber_android.ui.dialogs.post

import android.app.Activity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.Tags
import io.golos.cyber_android.ui.common.recycler_view.versioned.VersionedListItem
import io.golos.cyber_android.ui.dialogs.BottomSheetDialogFragmentBase
import io.golos.cyber_android.ui.dialogs.post.model.*
import io.golos.cyber_android.ui.dialogs.post.view.list.PostMenuAdapter
import io.golos.cyber_android.ui.shared_fragments.post.dto.PostHeader
import io.golos.domain.use_cases.post.post_dto.PostFormatVersion
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
        const val RESULT_JOIN = Activity.RESULT_FIRST_USER + 6
        const val RESULT_JOINED = Activity.RESULT_FIRST_USER + 7
        const val RESULT_REPORT = Activity.RESULT_FIRST_USER + 8


        private const val IS_MY_POST = "IS_MY_POST"
        private const val PAYLOAD = "PAYLOAD"
        private const val TYPE = "TYPE"
        private const val FORMAT_VERSION = "FORMAT_VERSION"

        private const val POST_MENU = "POST_MENU"

        fun newInstance(
            isMyPost: Boolean,
            type: PostType,
            formatVersion: PostFormatVersion,
            payload: String = ""
        ): PostPageMenuDialog {
            return PostPageMenuDialog().apply {
                arguments = Bundle().apply {
                    putSerializable(IS_MY_POST, isMyPost)
                    putString(PAYLOAD, payload)
                    putInt(TYPE, type.value)
                    putParcelable(FORMAT_VERSION, formatVersion)
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
        setSelectAction(RESULT_ADD_FAVORITE)
    }

    override fun onRemoveFromFavoriteItemClick() {
        setSelectAction(RESULT_REMOVE_FAVORITE)
    }

    override fun onShareItemClick(shareUrl: String) {
        setSelectAction(RESULT_SHARE) {
            putExtra(Tags.SHARE_URL, shareUrl)
        }
    }

    override fun onEditItemClick() {
        setSelectAction(RESULT_EDIT)
    }

    override fun onDeleteItemClick() {
        setSelectAction(RESULT_DELETE)
    }

    override fun onJoinItemClick(communityId: String) {
        setSelectAction(RESULT_JOIN) {
            putExtra(Tags.COMMUNITY_ID, communityId)
        }
    }

    override fun onJoinedItemClick(communityId: String) {
        setSelectAction(RESULT_JOINED) {
            putExtra(Tags.COMMUNITY_ID, communityId)
        }
    }

    override fun onReportItemClick() {
        setSelectAction(RESULT_REPORT)
    }

    override fun provideLayout(): Int = R.layout.dialog_post_menu

    override fun setupView() {
        val postMenu = arguments!!.getParcelable<PostMenu>(POST_MENU)

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
                    canEdit = false,
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
//        val isMyPost = arguments?.getBoolean(IS_MY_POST)
//        val type = PostType.create(arguments!!.getInt(TYPE))
//        val postFormat = arguments!!.getParcelable<PostFormatVersion>(FORMAT_VERSION)!!
//
//        val canEdit = type == PostType.BASIC && PostGlobalConstants.postFormatVersion.major >= postFormat.major
    }

    private fun generateMyPostMenu(postMenu: PostMenu): List<VersionedListItem> {
        val items = mutableListOf<VersionedListItem>()

        postMenu.shareUrl?.let { shareUrl ->
            items.add(ShareListItem(shareUrl))
        }
        items.add(EditListItem()) //todo add data to edit post
        items.add(DeleteListItem()) //todo add data to delete post

        return items
    }

    private fun generateNotMyPostMenu(postMenu: PostMenu): List<VersionedListItem> {
        val items = mutableListOf<VersionedListItem>()

        if (postMenu.isSubscribe) {
            items.add(JoinListItem(postMenu.communityId))
        } else {
            items.add(JoinedListItem(postMenu.communityId))
        }
        items.add(ReportListItem()) //todo add data to edit post

        return items
    }
}