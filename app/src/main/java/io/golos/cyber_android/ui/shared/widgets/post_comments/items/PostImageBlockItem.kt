package io.golos.cyber_android.ui.shared.widgets.post_comments.items

import android.content.Context
import android.view.View
import io.golos.cyber_android.ui.dto.ContentId
import io.golos.cyber_android.ui.shared.base.adapter.RecyclerItem
import io.golos.cyber_android.ui.shared.widgets.post_comments.EmbedImageWidget
import io.golos.cyber_android.ui.shared.widgets.post_comments.EmbedImageWidgetListener
import io.golos.domain.use_cases.post.post_dto.ImageBlock

class PostImageBlockItem(
    val imageBlock: ImageBlock,
    val contentId: ContentId? = null,
    widgetListener: EmbedImageWidgetListener? = null,
    onLongClickListener: View.OnLongClickListener? = null
) :
    BaseBlockItem<ImageBlock, EmbedImageWidgetListener, EmbedImageWidget>(imageBlock, widgetListener, onLongClickListener) {

    override fun createWidget(
        context: Context
    ): EmbedImageWidget = EmbedImageWidget(context).apply {
        setContentId(contentId)
    }

    override fun areItemsTheSame(): Int = imageBlock.hashCode()

    override fun areContentsSame(item: RecyclerItem): Boolean {
        if (item is PostImageBlockItem) {
            return imageBlock == item.imageBlock
        }
        return false
    }
}