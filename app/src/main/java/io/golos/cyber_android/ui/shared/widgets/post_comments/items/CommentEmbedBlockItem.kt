package io.golos.cyber_android.ui.shared.widgets.post_comments.items

import android.content.Context
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.shared.base.adapter.RecyclerItem
import io.golos.cyber_android.ui.shared.widgets.post_comments.EmbedWidget
import io.golos.cyber_android.ui.shared.widgets.post_comments.EmbedWidgetListener
import io.golos.domain.dto.ContentIdDomain
import io.golos.domain.posts_parsing_rendering.post_metadata.post_dto.EmbedBlock

class CommentEmbedBlockItem(
    val embedBlock: EmbedBlock,
    val contentId: ContentIdDomain,
    widgetListener: EmbedWidgetListener? = null
) : BaseBlockItem<EmbedBlock, EmbedWidgetListener, EmbedWidget>(
    embedBlock,
    widgetListener
) {

    override fun createWidget(
        context: Context
    ): EmbedWidget = EmbedWidget(context).apply {
        setContentId(contentId)
        val resources = context.resources
        val cornerRadius = resources.getDimension(R.dimen.comment_background_corners).toInt()
        setCornerRadius(cornerRadius)
        val commentImageBlockWidth = resources.getDimension(R.dimen.post_comments_width_max) - 2 * resources.getDimension(R.dimen.post_comments_text_horizontal_padding)
        setWidthBlock(commentImageBlockWidth.toInt())
        setPreloadFrameColor(R.color.comment_empty_place_holder)
    }

    override fun areItemsTheSame(): Int = embedBlock.hashCode()

    override fun areContentsSame(item: RecyclerItem): Boolean {
        if (item is CommentEmbedBlockItem) {
            return embedBlock == item.embedBlock
        }
        return false
    }
}