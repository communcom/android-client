package io.golos.cyber_android.ui.shared.widgets.post_comments.items

import android.content.Context
import android.view.View
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.shared.base.adapter.RecyclerItem
import io.golos.cyber_android.ui.shared.widgets.post_comments.EmbedImageWidget
import io.golos.cyber_android.ui.shared.widgets.post_comments.EmbedImageWidgetListener
import io.golos.domain.dto.ContentIdDomain
import io.golos.domain.posts_parsing_rendering.post_metadata.post_dto.ImageBlock

class CommentImageBlockItem(
    val imageBlock: ImageBlock,
    val contentId: ContentIdDomain? = null,
    widgetListener: EmbedImageWidgetListener? = null,
    onLongClickListener: View.OnLongClickListener? = null,
    onClickListener: View.OnClickListener? = null
) : BaseBlockItem<ImageBlock, EmbedImageWidgetListener, EmbedImageWidget>(imageBlock, widgetListener, onLongClickListener,onClickListener) {

    override fun createWidget(context: Context): EmbedImageWidget = EmbedImageWidget(context).apply {
        setContentId(contentId)
        val resources = context.resources
        val cornerRadius = resources.getDimension(R.dimen.comment_background_corners).toInt()
        setCornerRadius(cornerRadius)
        val commentImageBlockWidth = resources.getDimension(R.dimen.post_comments_width_max) - 2 * resources.getDimension(R.dimen.post_comments_text_horizontal_padding)
        setWidthBlock(commentImageBlockWidth.toInt())
        setPreloadFrameColor(R.color.comment_empty_place_holder)
    }

    override fun areItemsTheSame(): Int = imageBlock.hashCode()

    override fun areContentsSame(item: RecyclerItem): Boolean {
        if (item is CommentImageBlockItem) {
            return imageBlock == item.imageBlock
        }
        return false
    }
}