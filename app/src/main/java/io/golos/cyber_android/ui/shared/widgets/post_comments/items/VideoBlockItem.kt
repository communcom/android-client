package io.golos.cyber_android.ui.shared.widgets.post_comments.items

import android.content.Context
import io.golos.cyber_android.ui.shared.base.adapter.RecyclerItem
import io.golos.cyber_android.ui.shared.widgets.post_comments.EmbedVideoWidget
import io.golos.cyber_android.ui.shared.widgets.post_comments.EmbedVideoWidgetListener
import io.golos.domain.dto.ContentIdDomain
import io.golos.domain.posts_parsing_rendering.post_metadata.post_dto.VideoBlock

class VideoBlockItem(
    val videoBlock: VideoBlock,
    val contentId: ContentIdDomain? = null,
    widgetListener: EmbedVideoWidgetListener? = null
) : BaseBlockItem<VideoBlock, EmbedVideoWidgetListener, EmbedVideoWidget>(videoBlock, widgetListener) {

    override fun createWidget(context: Context): EmbedVideoWidget =
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