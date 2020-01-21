package io.golos.cyber_android.ui.screens.post_view.view.list.view_holders

import android.content.Context
import android.text.SpannableStringBuilder
import android.view.ViewGroup
import androidx.annotation.ColorInt
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.screens.post_view.dto.post_list_items.SecondLevelCommentCollapsedListItem
import io.golos.cyber_android.ui.screens.post_view.view_model.PostPageViewModelListEventsProcessor
import io.golos.cyber_android.ui.shared.characters.SpecialChars
import io.golos.cyber_android.ui.shared.extensions.getColorRes
import io.golos.cyber_android.ui.shared.recycler_view.ViewHolderBase
import kotlinx.android.synthetic.main.item_post_comment_second_level_collapsed.view.*

class SecondLevelCommentCollapsedViewHolder(
    parentView: ViewGroup
) : ViewHolderBase<PostPageViewModelListEventsProcessor, SecondLevelCommentCollapsedListItem>(
    parentView,
    R.layout.item_post_comment_second_level_collapsed
) {
    @ColorInt
    private val spansColor: Int = parentView.context.resources.getColorRes(R.color.default_clickable_span_color)

    override fun init(listItem: SecondLevelCommentCollapsedListItem, listItemEventsProcessor: PostPageViewModelListEventsProcessor) {
        itemView.replyText.text = getReplyText(itemView.context, listItem)
        itemView.setOnClickListener { listItemEventsProcessor.onCollapsedCommentsClick(listItem.parentCommentId) }
    }

    override fun release() {
        itemView.setOnClickListener(null)
    }

    private fun getReplyText(context: Context, listItem: SecondLevelCommentCollapsedListItem) : SpannableStringBuilder {
        val result = SpannableStringBuilder()

        with(listItem) {
            result.append(" ${SpecialChars.BULLET} ")
            result.append(totalChild.toString())
            result.append(" ")
            result.append(context.resources.getQuantityText(R.plurals.reply, totalChild.toInt()))
        }

        return result
    }
}