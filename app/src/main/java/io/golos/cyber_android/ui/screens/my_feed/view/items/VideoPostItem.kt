package io.golos.cyber_android.ui.screens.my_feed.view.items

import android.content.Context
import io.golos.cyber_android.ui.common.base.adapter.RecyclerItem
import io.golos.cyber_android.ui.common.widgets.post.EmbedVideoWidget
import io.golos.cyber_android.ui.common.widgets.post.EmbedVideoWidgetListener
import io.golos.domain.use_cases.post.post_dto.VideoBlock

class VideoPostItem(val videoBlock: VideoBlock, widgetListener: EmbedVideoWidgetListener?) :
    BasePostBlockItem<VideoBlock, EmbedVideoWidgetListener, EmbedVideoWidget>(videoBlock, widgetListener) {

    override fun createWidgetView(context: Context): EmbedVideoWidget =
        EmbedVideoWidget(context).apply { disableHtmlContent() }

    override fun areItemsTheSame(): Int = videoBlock.hashCode()

    override fun areContentsSame(item: RecyclerItem): Boolean {
        if (item is VideoPostItem) {
            return videoBlock == item.videoBlock
        }
        return false
    }
}