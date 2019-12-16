package io.golos.cyber_android.ui.common.widgets.post_comments.items

import android.content.Context
import io.golos.cyber_android.ui.common.base.adapter.RecyclerItem
import io.golos.cyber_android.ui.common.widgets.post_comments.AttachmentWidgetListener
import io.golos.cyber_android.ui.common.widgets.post_comments.AttachmentsWidget
import io.golos.domain.use_cases.post.post_dto.AttachmentsBlock

class AttachmentBlockItem(
    val attachmentsBlock: AttachmentsBlock,
    widgetListener: AttachmentWidgetListener? = null
) : BaseBlockItem<AttachmentsBlock, AttachmentWidgetListener, AttachmentsWidget>(
    attachmentsBlock,
    widgetListener
) {

    override fun createWidgetView(context: Context): AttachmentsWidget = AttachmentsWidget(context)

    override fun areItemsTheSame(): Int = attachmentsBlock.hashCode()

    override fun areContentsSame(item: RecyclerItem): Boolean {
        if (item is AttachmentBlockItem) {
            return attachmentsBlock == item.attachmentsBlock
        }
        return false
    }
}