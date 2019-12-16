package io.golos.cyber_android.ui.common.widgets.post_comments.items

import android.content.Context
import io.golos.cyber_android.ui.common.base.adapter.RecyclerItem
import io.golos.cyber_android.ui.common.widgets.post_comments.EmbedWebsiteWidget
import io.golos.cyber_android.ui.common.widgets.post_comments.EmbedWebsiteWidgetListener
import io.golos.domain.use_cases.post.post_dto.WebsiteBlock

class WebSiteBlockItem(val websiteBlock: WebsiteBlock, widgetListener: EmbedWebsiteWidgetListener?) :
    BaseBlockItem<WebsiteBlock, EmbedWebsiteWidgetListener, EmbedWebsiteWidget>(websiteBlock, widgetListener) {

    override fun createWidgetView(context: Context): EmbedWebsiteWidget =
        EmbedWebsiteWidget(context)

    override fun areItemsTheSame(): Int = websiteBlock.hashCode()

    override fun areContentsSame(item: RecyclerItem): Boolean {
        if (item is WebSiteBlockItem) {
            return websiteBlock == item.websiteBlock
        }
        return false
    }
}