package io.golos.cyber_android.ui.screens.my_feed.view.items

import android.content.Context
import io.golos.cyber_android.ui.common.base.adapter.RecyclerItem
import io.golos.cyber_android.ui.shared_fragments.post.view.list.view_holders.post_body.widgets.EmbedWebsiteWidget
import io.golos.cyber_android.ui.shared_fragments.post.view.list.view_holders.post_body.widgets.EmbedWebsiteWidgetListener
import io.golos.domain.use_cases.post.post_dto.WebsiteBlock

class WebSitePostItem(val websiteBlock: WebsiteBlock, widgetListener: EmbedWebsiteWidgetListener?) :
    BasePostBlockItem<WebsiteBlock, EmbedWebsiteWidgetListener, EmbedWebsiteWidget>(websiteBlock, widgetListener) {

    override fun createWidgetView(context: Context): EmbedWebsiteWidget = EmbedWebsiteWidget(context)

    override fun areItemsTheSame(): Int = websiteBlock.hashCode()

    override fun areContentsSame(item: RecyclerItem): Boolean {
        if(item is WebSitePostItem){
            return websiteBlock == item.websiteBlock
        }
        return false
    }
}