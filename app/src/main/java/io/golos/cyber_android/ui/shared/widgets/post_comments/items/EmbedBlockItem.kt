package io.golos.cyber_android.ui.shared.widgets.post_comments.items

import android.content.Context
import io.golos.cyber_android.ui.shared.base.adapter.RecyclerItem
import io.golos.cyber_android.ui.shared.widgets.post_comments.EmbedWidget
import io.golos.cyber_android.ui.shared.widgets.post_comments.EmbedWidgetListener
import io.golos.cyber_android.ui.dto.ContentId
import io.golos.domain.use_cases.post.post_dto.EmbedBlock

class EmbedBlockItem(
    val embedBlock: EmbedBlock,
    val contentId: ContentId,
    widgetListener: EmbedWidgetListener? = null
) : BaseBlockItem<EmbedBlock, EmbedWidgetListener, EmbedWidget>(
    embedBlock,
    widgetListener
) {

    override fun createWidget(
        context: Context
    ): EmbedWidget = EmbedWidget(context).apply {
        setContentId(contentId)
    }

    override fun areItemsTheSame(): Int = embedBlock.hashCode()

    override fun areContentsSame(item: RecyclerItem): Boolean {
        if (item is EmbedBlockItem) {
            return embedBlock == item.embedBlock
        }
        return false
    }
}