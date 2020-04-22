package io.golos.cyber_android.ui.shared.widgets.post_comments.items

import android.content.Context
import android.view.View
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.dto.ContentId
import io.golos.cyber_android.ui.shared.base.adapter.RecyclerItem
import io.golos.cyber_android.ui.shared.widgets.post_comments.ParagraphWidget
import io.golos.cyber_android.ui.shared.widgets.post_comments.ParagraphWidgetListener
import io.golos.domain.posts_parsing_rendering.post_metadata.post_dto.ParagraphBlock
import kotlinx.android.synthetic.main.item_post_block.view.*

data class PostParagraphBlockItem(
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
        val verticalPadding = 0
        val horizontalPadding = context.resources.getDimension(R.dimen.post_content_border_horizontal).toInt()
        setPadding(horizontalPadding, verticalPadding, horizontalPadding, verticalPadding)
    }

    override fun initView(context: Context, view: View) {
        super.initView(context, view)
        view.postWidgetContainer.setOnClickListener {
            widgetListener?.onBodyClicked(contentId)
        }
    }

    override fun areItemsTheSame(): Int = paragraphBlock.hashCode()

    override fun areContentsSame(item: RecyclerItem): Boolean {
        if (item is PostParagraphBlockItem) {
            return paragraphBlock == item.paragraphBlock
        }
        return false
    }
}