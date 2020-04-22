package io.golos.cyber_android.ui.shared.widgets.post_comments.items

import android.content.Context
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.dto.ContentId
import io.golos.cyber_android.ui.shared.base.adapter.RecyclerItem
import io.golos.cyber_android.ui.shared.widgets.post_comments.RichWidget
import io.golos.cyber_android.ui.shared.widgets.post_comments.RichWidgetListener
import io.golos.domain.posts_parsing_rendering.post_metadata.post_dto.RichBlock

class CommentRichBlockItem(
    val richBlock: RichBlock,
    val contentId: ContentId,
    widgetListener: RichWidgetListener? = null
) : BaseBlockItem<RichBlock, RichWidgetListener, RichWidget>(
    richBlock,
    widgetListener
) {

    override fun createWidget(
        context: Context
    ): RichWidget = RichWidget(context).apply {
        setContentId(contentId)
        val resources = context.resources
        val cornerRadius = resources.getDimension(R.dimen.comment_background_corners).toInt()
        setCornerRadius(cornerRadius)
        val commentImageBlockWidth = resources.getDimension(R.dimen.post_comments_width_max) - 2 * resources.getDimension(R.dimen.post_comments_text_horizontal_padding)
        setWidthBlock(commentImageBlockWidth.toInt())
        setPreloadFrameColor(R.color.comment_empty_place_holder)
    }

    override fun areItemsTheSame(): Int = richBlock.hashCode()

    override fun areContentsSame(item: RecyclerItem): Boolean {
        if (item is PostRichBlockItem) {
            return richBlock == item.richBlock
        }
        return false
    }
}