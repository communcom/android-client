package io.golos.cyber_android.ui.screens.main_activity.feed.my_feed.view.items

import android.content.Context
import io.golos.cyber_android.ui.screens.main_activity.feed.my_feed.view_model.MyFeedViewModelListEventsProcessor
import io.golos.cyber_android.ui.shared_fragments.post.view.list.view_holders.post_body.widgets.EmbedWebsiteWidget
import io.golos.domain.use_cases.post.post_dto.WebsiteBlock

class WebSitePostItem(websiteBlock: WebsiteBlock, processor: MyFeedViewModelListEventsProcessor?) :
    BasePostBlockItem<WebsiteBlock, EmbedWebsiteWidget>(websiteBlock, processor) {

    override fun createWidgetView(context: Context): EmbedWebsiteWidget = EmbedWebsiteWidget(context)
}