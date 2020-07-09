package io.golos.cyber_android.ui.shared.widgets.post_comments.items

import android.content.Context
import android.view.View
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.shared.base.adapter.RecyclerItem
import io.golos.cyber_android.ui.shared.widgets.post_comments.ParagraphSetWidget
import io.golos.cyber_android.ui.shared.widgets.post_comments.ParagraphWidget
import io.golos.cyber_android.ui.shared.widgets.post_comments.ParagraphWidgetListener
import io.golos.domain.dto.ContentIdDomain
import io.golos.domain.posts_parsing_rendering.post_metadata.post_dto.ParagraphBlock
import io.golos.domain.posts_parsing_rendering.post_metadata.post_dto.ParagraphSet

data class CommentParagraphSetItem(
    val paragraphSet: ParagraphSet,
    val widgetListener: ParagraphWidgetListener?,
    val contentId: ContentIdDomain,
    val onLongClickListener: View.OnLongClickListener? = null
) : BaseBlockItem<ParagraphSet, ParagraphWidgetListener, ParagraphSetWidget>(
    paragraphSet,
    widgetListener,
    onLongClickListener) {

    override fun createWidget(context: Context): ParagraphSetWidget = ParagraphSetWidget(context).apply {
        setSeeMoreEnabled(true)
        setContentId(contentId)
        val verticalMargin = context.resources.getDimension(R.dimen.post_comments_text_vertical_padding).toInt()
        val horizontalPadding = context.resources.getDimension(R.dimen.post_comments_text_horizontal_padding).toInt()
        setPadding(0, 0, 0, 0)
    }

    override fun areItemsTheSame(): Int = paragraphSet.hashCode()

    override fun areContentsSame(item: RecyclerItem): Boolean {
        if (item is CommentParagraphSetItem) {
            return paragraphSet == item.paragraphSet
        }
        return false
    }
}