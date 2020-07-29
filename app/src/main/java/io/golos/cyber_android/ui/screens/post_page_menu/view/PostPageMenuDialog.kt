package io.golos.cyber_android.ui.screens.post_page_menu.view

import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.dialogs.base.BottomSheetDialogFragmentBase
import io.golos.cyber_android.ui.screens.post_page_menu.model.*
import io.golos.cyber_android.ui.screens.post_page_menu.view.list.PostMenuAdapter
import io.golos.cyber_android.ui.screens.post_view.dto.PostHeader
import io.golos.cyber_android.ui.shared.recycler_view.versioned.VersionedListItem
import kotlinx.android.synthetic.main.dialog_post_menu.*

/**
 * [BottomSheetDialogFragment] that shows post menu
 */
class PostPageMenuDialog(
    private val postMenu: PostMenu
) : BottomSheetDialogFragmentBase<PostPageMenuDialog.Result>(),
    PostMenuModelListEventProcessor {

    sealed class Result {
        data class AddFavorite(val postMenu: PostMenu): Result()
        data class ViewInExplorer(val postMenu: PostMenu):Result()
        data class RemoveFavorite(val postMenu: PostMenu): Result()
        data class Share(val postMenu: PostMenu): Result()
        data class Edit(val postMenu: PostMenu): Result()
        data class Delete(val postMenu: PostMenu): Result()
        data class Subscribe(val postMenu: PostMenu): Result()
        data class Unsubscribe(val postMenu: PostMenu): Result()
        data class Report(val postMenu: PostMenu): Result()
    }

    private var isPostSubscriptionModified = false

    companion object {
        fun show(parent: Fragment,isPostSubscriptionModified:Boolean, postMenu: PostMenu, closeAction: (Result?) -> Unit) =
            PostPageMenuDialog(postMenu)
                .apply {
                    closeActionListener = closeAction
                    this.isPostSubscriptionModified = isPostSubscriptionModified
                }
                .show(parent.parentFragmentManager, "POST_PAGE_MENU_DIALOG")
    }


    override val closeButton: View?
        get() = ivClose

    override val layout: Int
        get() = R.layout.dialog_post_menu

    override fun onAddToFavoriteItemClick() = closeOnItemSelected(Result.AddFavorite(postMenu))

    override fun onRemoveFromFavoriteItemClick() = closeOnItemSelected(Result.RemoveFavorite(postMenu))

    override fun onShareItemClick() = closeOnItemSelected(Result.Share(postMenu))

    override fun onEditItemClick() = closeOnItemSelected(Result.Edit(postMenu))

    override fun onDeleteItemClick() = closeOnItemSelected(Result.Delete(postMenu))

    override fun onJoinItemClick() = closeOnItemSelected(Result.Subscribe(postMenu))

    override fun onJoinedItemClick() = closeOnItemSelected(Result.Unsubscribe(postMenu))

    override fun onReportItemClick() = closeOnItemSelected(Result.Report(postMenu))

    override fun onViewInExplorerClick() = closeOnItemSelected(Result.ViewInExplorer(postMenu))

    override fun setupView() {
        postMenu.let { menu ->
            val adapter = PostMenuAdapter(this)
            rvMenu.layoutManager = LinearLayoutManager(requireContext())
            rvMenu.adapter = adapter

            postMenuHeader.setHeader(
                PostHeader(
                    communityName = menu.communityName,
                    communityAvatarUrl = menu.communityAvatarUrl,
                    communityId = menu.communityId,
                    actionDateTime = menu.creationTime,
                    userName = menu.authorUsername,
                    userId = menu.authorUserId,
                    userAvatarUrl = menu.authorAvatarUrl,
                    canJoinToCommunity = false,
                    isBackFeatureEnabled = false,
                    reward = null
                )
            )
            postMenuHeader.hideActionMenu()

            val isMyPost = menu.isMyPost
            val listOfItems =
                if (isMyPost) generateMyPostMenu(menu)
                else generateNotMyPostMenu(menu)
            adapter.update(listOfItems)
        }
    }

    private fun generateMyPostMenu(postMenu: PostMenu): List<VersionedListItem> {
        val items = mutableListOf<VersionedListItem>()

        postMenu.shareUrl?.let { shareUrl ->
            items.add(ShareListItem(shareUrl))
        }
        items.add(ShowInExplorerListItem())
        items.add(EditListItem())
        items.add(DeleteListItem())

        return items
    }

    private fun generateNotMyPostMenu(postMenu: PostMenu): List<VersionedListItem> {
        val items = mutableListOf<VersionedListItem>()

        if (isPostSubscriptionModified) {
            if (postMenu.isSubscribed) {
                items.add(JoinListItem())
            } else {
                items.add(JoinedListItem())
            }
        } else {
            if (postMenu.isSubscribed) {
                items.add(JoinedListItem())
            } else {
                items.add(JoinListItem())
            }

        }
        items.add(ShowInExplorerListItem())
        items.add(ReportListItem())

        return items
    }
}