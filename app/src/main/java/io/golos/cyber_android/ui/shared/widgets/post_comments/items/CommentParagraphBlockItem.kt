package io.golos.cyber_android.ui.shared.widgets.post_comments.items

import android.content.Context
import android.view.View
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.dto.ContentId
import io.golos.cyber_android.ui.shared.base.adapter.RecyclerItem
import io.golos.cyber_android.ui.shared.widgets.post_comments.ParagraphWidget
import io.golos.cyber_android.ui.shared.widgets.post_comments.ParagraphWidgetListener
import io.golos.domain.use_cases.post.post_dto.ParagraphBlock

data class CommentParagraphBlockItem(
    val paragraphBlock: ParagraphBlock,
    val widgetListener: ParagraphWidgetListener?,
    val contentId: ContentId,
    val onLongClickListener: View.OnLongClickListener? = null
) : BaseBlockItem<ParagraphBlock, ParagraphWidgetListener, ParagraphWidget>(
    paragraphBlock,
    widgetListener,
    onLongClickListener) {

    override fun createWidget(
        context: Context
    ): ParagraphWidget = ParagraphWidget(context).apply {
        setSeeMoreEnabled(true)
        setContentId(contentId)
        val verticalMargin = context.resources.getDimension(R.dimen.comment_paragraph_block_horizontal_margin).toInt()
        setMargin(0, verticalMargin, 0, verticalMargin)
    }

    override fun areItemsTheSame(): Int = paragraphBlock.hashCode()

    override fun areContentsSame(item: RecyclerItem): Boolean {
        if (item is CommentParagraphBlockItem) {
            return paragraphBlock == item.paragraphBlock
        }
        return false
    }
}