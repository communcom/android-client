package io.golos.cyber_android.ui.common.widgets.post_comments.items

import android.content.Context
import io.golos.cyber_android.ui.common.base.adapter.RecyclerItem
import io.golos.cyber_android.ui.common.widgets.post_comments.EmbedImageWidget
import io.golos.cyber_android.ui.common.widgets.post_comments.EmbedImageWidgetListener
import io.golos.cyber_android.ui.dto.ContentId
import io.golos.domain.use_cases.post.post_dto.ImageBlock

class ImageBlockItem(
    val imageBlock: ImageBlock,
    val contentId: ContentId? = null,
    widgetListener: EmbedImageWidgetListener? = null
) :
    BaseBlockItem<ImageBlock, EmbedImageWidgetListener, EmbedImageWidget>(imageBlock, widgetListener) {

    override fun createWidgetView(
        context: Context
    ): EmbedImageWidget = EmbedImageWidget(context).apply {
        setContentId(contentId)
    }

    override fun areItemsTheSame(): Int = imageBlock.hashCode()

    override fun areContentsSame(item: RecyclerItem): Boolean {
        if (item is ImageBlockItem) {
            return imageBlock == item.imageBlock
        }
        return false
    }
}