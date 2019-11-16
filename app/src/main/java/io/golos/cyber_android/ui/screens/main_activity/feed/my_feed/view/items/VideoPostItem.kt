package io.golos.cyber_android.ui.screens.main_activity.feed.my_feed.view.items

import android.content.Context
import io.golos.cyber_android.ui.shared_fragments.post.view.list.view_holders.post_body.widgets.EmbedVideoWidget
import io.golos.cyber_android.ui.shared_fragments.post.view.list.view_holders.post_body.widgets.EmbedVideoWidgetListener
import io.golos.domain.use_cases.post.post_dto.VideoBlock

class VideoPostItem(videoBlock: VideoBlock, widgetListener: EmbedVideoWidgetListener?) :
    BasePostBlockItem<VideoBlock, EmbedVideoWidgetListener, EmbedVideoWidget>(videoBlock, widgetListener) {

    override fun createWidgetView(context: Context): EmbedVideoWidget = EmbedVideoWidget(context)
}