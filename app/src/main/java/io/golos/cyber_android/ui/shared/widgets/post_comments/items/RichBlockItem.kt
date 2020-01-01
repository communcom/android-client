package io.golos.cyber_android.ui.shared.widgets.post_comments.items

import android.content.Context
import io.golos.cyber_android.ui.shared.base.adapter.RecyclerItem
import io.golos.cyber_android.ui.shared.widgets.post_comments.RichWidget
import io.golos.cyber_android.ui.shared.widgets.post_comments.RichWidgetListener
import io.golos.cyber_android.ui.dto.ContentId
import io.golos.domain.use_cases.post.post_dto.RichBlock

class RichBlockItem(
    val richBlock: RichBlock,
    val contentId: ContentId,
    widgetListener: RichWidgetListener? = null
) : BaseBlockItem<RichBlock, RichWidgetListener, RichWidget>(
    richBlock,
    widgetListener
) {

    override fun createWidget(
        context: Context
    ): RichWidget = RichWidget(context).apply {
        setContentId(contentId)
    }

    override fun areItemsTheSame(): Int = richBlock.hashCode()

    override fun areContentsSame(item: RecyclerItem): Boolean {
        if (item is RichBlockItem) {
            return richBlock == item.richBlock
        }
        return false
    }
}