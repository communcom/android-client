package io.golos.cyber_android.ui.screens.my_feed.view.items

import android.content.Context
import io.golos.cyber_android.ui.common.base.adapter.RecyclerItem
import io.golos.cyber_android.ui.common.widgets.post.RichWidget
import io.golos.cyber_android.ui.common.widgets.post.RichWidgetListener
import io.golos.cyber_android.ui.dto.ContentId
import io.golos.cyber_android.ui.dto.Post
import io.golos.domain.use_cases.post.post_dto.RichBlock

class RichPostItem(
    val richBlock: RichBlock,
    val contentId: ContentId,
    widgetListener: RichWidgetListener? = null
) : BasePostBlockItem<RichBlock, RichWidgetListener, RichWidget>(
    richBlock,
    widgetListener
) {

    override fun createWidgetView(
        context: Context
    ): RichWidget = RichWidget(context).apply {
        setContentId(contentId)
    }

    override fun areItemsTheSame(): Int = richBlock.hashCode()

    override fun areContentsSame(item: RecyclerItem): Boolean {
        if (item is RichPostItem) {
            return richBlock == item.richBlock
        }
        return false
    }
}