package io.golos.cyber_android.ui.shared.widgets.post_comments.items

import android.content.Context
import android.view.View
import io.golos.cyber_android.ui.shared.base.adapter.RecyclerItem
import io.golos.cyber_android.ui.shared.utils.getScreenSize
import io.golos.cyber_android.ui.shared.widgets.post_comments.EmbedImageWidget
import io.golos.cyber_android.ui.shared.widgets.post_comments.EmbedImageWidgetListener
import io.golos.domain.dto.ContentIdDomain
import io.golos.domain.posts_parsing_rendering.post_metadata.post_dto.ImageBlock

class PostImageBlockItem(
    val imageBlock: ImageBlock,
    val contentId: ContentIdDomain? = null,
    widgetListener: EmbedImageWidgetListener? = null,
    onLongClickListener: View.OnLongClickListener? = null
) :
    BaseBlockItem<ImageBlock, EmbedImageWidgetListener, EmbedImageWidget>(imageBlock, widgetListener, onLongClickListener) {

    override fun createWidget(
        context: Context
    ): EmbedImageWidget = EmbedImageWidget(context).apply {
        setContentId(contentId)
        setWidthBlock(context.getScreenSize().x)
    }

    override fun areItemsTheSame(): Int = imageBlock.hashCode()

    override fun areContentsSame(item: RecyclerItem): Boolean {
        if (item is PostImageBlockItem) {
            return imageBlock == item.imageBlock
        }
        return false
    }
}