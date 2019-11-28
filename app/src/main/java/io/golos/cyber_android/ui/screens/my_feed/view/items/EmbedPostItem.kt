package io.golos.cyber_android.ui.screens.my_feed.view.items

import android.content.Context
import io.golos.cyber_android.ui.common.base.adapter.RecyclerItem
import io.golos.cyber_android.ui.common.widgets.post.EmbedWidget
import io.golos.cyber_android.ui.common.widgets.post.EmbedWidgetListener
import io.golos.domain.use_cases.post.post_dto.EmbedBlock

class EmbedPostItem(
    val embedBlock: EmbedBlock,
    widgetListener: EmbedWidgetListener? = null
) : BasePostBlockItem<EmbedBlock, EmbedWidgetListener, EmbedWidget>(
    embedBlock,
    widgetListener
) {

    override fun createWidgetView(context: Context): EmbedWidget = EmbedWidget(context)

    override fun areItemsTheSame(): Int = embedBlock.hashCode()

    override fun areContentsSame(item: RecyclerItem): Boolean {
        if (item is EmbedPostItem) {
            return embedBlock == item.embedBlock
        }
        return false
    }
}