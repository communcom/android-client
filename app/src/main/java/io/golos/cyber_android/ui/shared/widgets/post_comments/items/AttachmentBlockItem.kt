package io.golos.cyber_android.ui.shared.widgets.post_comments.items

import android.content.Context
import io.golos.cyber_android.ui.shared.base.adapter.RecyclerItem
import io.golos.cyber_android.ui.shared.widgets.post_comments.AttachmentWidgetListener
import io.golos.cyber_android.ui.shared.widgets.post_comments.AttachmentsWidget
import io.golos.domain.posts_parsing_rendering.post_metadata.post_dto.AttachmentsBlock

class AttachmentBlockItem(
    val attachmentsBlock: AttachmentsBlock,
    widgetListener: AttachmentWidgetListener? = null
) : BaseBlockItem<AttachmentsBlock, AttachmentWidgetListener, AttachmentsWidget>(
    attachmentsBlock,
    widgetListener
) {

    override fun createWidget(context: Context): AttachmentsWidget = AttachmentsWidget(context)

    override fun areItemsTheSame(): Int = attachmentsBlock.hashCode()

    override fun areContentsSame(item: RecyclerItem): Boolean {
        if (item is AttachmentBlockItem) {
            return attachmentsBlock == item.attachmentsBlock
        }
        return false
    }
}