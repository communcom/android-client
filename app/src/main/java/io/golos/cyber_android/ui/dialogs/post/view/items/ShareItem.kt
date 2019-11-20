package io.golos.cyber_android.ui.dialogs.post.view.items

import android.view.ViewGroup
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.common.recycler_view.ViewHolderBase
import io.golos.cyber_android.ui.dialogs.post.model.PostMenuModelListEventProcessor
import io.golos.cyber_android.ui.dialogs.post.model.ShareListItem
import io.golos.cyber_android.utils.setDrawableToEnd
import io.golos.cyber_android.utils.setStyle
import kotlinx.android.synthetic.main.item_post_menu.view.*

class ShareItem(
    parentView: ViewGroup
) : ViewHolderBase<PostMenuModelListEventProcessor, ShareListItem>(
    parentView,
    R.layout.item_post_menu
) {

    override fun init(listItem: ShareListItem, listItemEventsProcessor: PostMenuModelListEventProcessor) {
        with(itemView) {
            menuAction.text = context.getString(R.string.share_post)
            menuAction.setDrawableToEnd(R.drawable.ic_share)
            menuAction.setStyle(R.style.BottomSheetMenuItem)
            menuAction.setOnClickListener {
                listItemEventsProcessor.onShareItemClick(listItem.shareUrl)
            }
        }
    }

    override fun release() {
        itemView.menuAction.setOnClickListener(null)
    }
}