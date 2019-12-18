package io.golos.cyber_android.ui.shared_fragments.post.view.list.view_holders

import android.content.Context
import android.text.SpannableStringBuilder
import android.view.ViewGroup
import androidx.annotation.ColorInt
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.common.characters.SpecialChars
import io.golos.cyber_android.ui.common.extensions.getColorRes
import io.golos.cyber_android.ui.common.recycler_view.ViewHolderBase
import io.golos.cyber_android.ui.shared_fragments.post.dto.post_list_items.SecondLevelCommentCollapsedListItem
import io.golos.cyber_android.ui.shared_fragments.post.view_model.PostPageViewModelListEventsProcessor
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
        loadAvatarIcon(listItem.topCommentAuthor.avatarUrl)
        itemView.replyText.text = getReplyText(itemView.context, listItem)

        itemView.setOnClickListener { listItemEventsProcessor.onCollapsedCommentsClick(listItem.parentCommentId) }
    }

    override fun release() {
        itemView.setOnClickListener(null)
    }

    private fun loadAvatarIcon(avatarUrl: String?) {
        if (avatarUrl != null) {
            Glide.with(itemView).load(avatarUrl)
        } else {
            Glide.with(itemView).load(R.drawable.ic_empty_user)
        }.apply {
            apply(RequestOptions.circleCropTransform())
            into(itemView.ivAttachImage)
        }
    }

    private fun getReplyText(context: Context, listItem: SecondLevelCommentCollapsedListItem) : SpannableStringBuilder {
        val result = SpannableStringBuilder()

        with(listItem) {
            result.append(topCommentAuthor.username)

            result.append(" ")
            result.append(context.resources.getString(R.string.comment_answer))

            result.append(" ${SpecialChars.bullet} ")

            result.append(totalChild.toString())
            result.append(" ")
            result.append(context.resources.getQuantityText(R.plurals.reply, totalChild.toInt()))
        }

        return result
    }
}