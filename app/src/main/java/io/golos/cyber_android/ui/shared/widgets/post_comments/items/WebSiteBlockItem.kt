package io.golos.cyber_android.ui.shared.widgets.post_comments.items

import android.content.Context
import android.view.View
import io.golos.cyber_android.ui.shared.base.adapter.RecyclerItem
import io.golos.cyber_android.ui.shared.widgets.post_comments.EmbedWebsiteWidget
import io.golos.cyber_android.ui.shared.widgets.post_comments.EmbedWebsiteWidgetListener
import io.golos.domain.posts_parsing_rendering.post_metadata.post_dto.WebsiteBlock

class WebSiteBlockItem(val websiteBlock: WebsiteBlock, widgetListener: EmbedWebsiteWidgetListener?, onClickListener: View.OnClickListener? = null) :
    BaseBlockItem<WebsiteBlock, EmbedWebsiteWidgetListener, EmbedWebsiteWidget>(websiteBlock, widgetListener,onClickListener = onClickListener) {

    override fun createWidget(context: Context): EmbedWebsiteWidget =
        EmbedWebsiteWidget(context)

    override fun areItemsTheSame(): Int = websiteBlock.hashCode()

    override fun areContentsSame(item: RecyclerItem): Boolean {
        if (item is WebSiteBlockItem) {
            return websiteBlock == item.websiteBlock
        }
        return false
    }
}