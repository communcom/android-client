package io.golos.cyber_android.ui.screens.main_activity.feed.my_feed.view.items

import android.content.Context
import io.golos.cyber_android.ui.shared_fragments.post.view.list.view_holders.post_body.widgets.AttachmentWidgetListener
import io.golos.cyber_android.ui.shared_fragments.post.view.list.view_holders.post_body.widgets.AttachmentsWidget
import io.golos.domain.use_cases.post.post_dto.AttachmentsBlock

class AttachmentPostItem(
    attachmentsBlock: AttachmentsBlock,
    widgetListener: AttachmentWidgetListener? = null
) :
    BasePostBlockItem<AttachmentsBlock, AttachmentWidgetListener, AttachmentsWidget>(attachmentsBlock, widgetListener) {

    override fun createWidgetView(context: Context): AttachmentsWidget = AttachmentsWidget(context)
}