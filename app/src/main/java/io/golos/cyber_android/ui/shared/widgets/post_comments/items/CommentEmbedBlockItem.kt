package io.golos.cyber_android.ui.shared.widgets.post_comments.items

import android.content.Context
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.dto.ContentId
import io.golos.cyber_android.ui.shared.base.adapter.RecyclerItem
import io.golos.cyber_android.ui.shared.utils.getScreenSize
import io.golos.cyber_android.ui.shared.widgets.post_comments.EmbedWidget
import io.golos.cyber_android.ui.shared.widgets.post_comments.EmbedWidgetListener
import io.golos.domain.use_cases.post.post_dto.EmbedBlock

class CommentEmbedBlockItem(
    val embedBlock: EmbedBlock,
    val contentId: ContentId,
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
        val cornerRadius = resources.getDimension(R.dimen.comment_image_block_round_corners).toInt()
        setCornerRadius(cornerRadius)
        val commentImageBlockWidth = resources.getDimension(R.dimen.post_comments_width) - 2 * resources.getDimension(R.dimen.post_comments_text_horizontal_padding)
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