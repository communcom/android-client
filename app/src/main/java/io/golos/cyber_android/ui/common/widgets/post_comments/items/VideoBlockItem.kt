package io.golos.cyber_android.ui.common.widgets.post_comments.items

import android.content.Context
import io.golos.cyber_android.ui.common.base.adapter.RecyclerItem
import io.golos.cyber_android.ui.common.widgets.post_comments.EmbedVideoWidget
import io.golos.cyber_android.ui.common.widgets.post_comments.EmbedVideoWidgetListener
import io.golos.cyber_android.ui.dto.ContentId
import io.golos.domain.use_cases.post.post_dto.VideoBlock

class VideoBlockItem(
    val videoBlock: VideoBlock,
    val contentId: ContentId? = null,
    widgetListener: EmbedVideoWidgetListener? = null
) : BaseBlockItem<VideoBlock, EmbedVideoWidgetListener, EmbedVideoWidget>(videoBlock, widgetListener) {

    override fun createWidgetView(context: Context): EmbedVideoWidget =
        EmbedVideoWidget(context).apply {
            disableHtmlContent()
            setContentId(contentId)
        }

    override fun areItemsTheSame(): Int = videoBlock.hashCode()

    override fun areContentsSame(item: RecyclerItem): Boolean {
        if (item is VideoBlockItem) {
            return videoBlock == item.videoBlock
        }
        return false
    }
}