package io.golos.cyber_android.ui.screens.notifications.view.list.view_holders

import android.graphics.Typeface
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.dto.ContentId
import io.golos.cyber_android.ui.mappers.mapToContentId
import io.golos.cyber_android.ui.screens.notifications.view.list.items.NotificationUpVoteItem
import io.golos.cyber_android.ui.screens.notifications.view_model.NotificationsViewModelListEventsProcessor
import io.golos.cyber_android.ui.shared.glide.loadNotificationImageContent
import io.golos.cyber_android.ui.shared.spans.ColorTextClickableSpan
import io.golos.domain.dto.UserIdDomain
import io.golos.domain.extensions.appendText
import io.golos.domain.extensions.setSpan
import io.golos.utils.SPACE
import kotlinx.android.synthetic.main.item_notification.view.*

class NotificationUpVoteViewHolder(
    parentView: ViewGroup
) : BaseNotificationViewHolder<NotificationUpVoteItem>(
    parentView
) {
    override val notificationTypeLabelResId: Int = R.drawable.ic_up_vote_label

    override fun init(listItem: NotificationUpVoteItem, listItemEventsProcessor: NotificationsViewModelListEventsProcessor) {
        super.init(listItem, listItemEventsProcessor)
        setAction(listItem, listItemEventsProcessor)
        setMessage(listItem, listItemEventsProcessor)
    }

    private fun setMessage(listItem: NotificationUpVoteItem, listItemEventsProcessor: NotificationsViewModelListEventsProcessor) {
        val context = itemView.context
        val messageStringBuilder = SpannableStringBuilder()
        val userName = listItem.userName
        val userId = listItem.userId
        val colorMessage = ContextCompat.getColor(context, R.color.black)

        userName?.let {
            val userNameInterval = messageStringBuilder.appendText(it)
            messageStringBuilder.setSpan(StyleSpan(Typeface.BOLD), userNameInterval)
            messageStringBuilder.setSpan(object : ColorTextClickableSpan(userId, colorMessage) {

                override fun onClick(spanData: String) {
                    super.onClick(spanData)
                    listItemEventsProcessor.onUserClickedById(UserIdDomain(spanData))
                }
            }, userNameInterval)
            messageStringBuilder.append(SPACE)
        }

        fun setUpVoteMessage(message: String) {
            val boxedMessage = context.getString(R.string.up_vote_message_content, message)
            val messageInterval = messageStringBuilder.appendText(boxedMessage)
            messageStringBuilder.setSpan(ForegroundColorSpan(colorMessage), messageInterval)
        }

        if (listItem.comment != null) {
            val messageLabel = context.getString(R.string.liked_your_comment)
            val messageLabelInterval = messageStringBuilder.appendText(messageLabel)
            messageStringBuilder.setSpan(ForegroundColorSpan(colorMessage), messageLabelInterval)
            listItem.comment.shortText?.let {
                setUpVoteMessage(it)
            }
        } else if (listItem.post != null) {
            val messageLabel = context.getString(R.string.liked_your_post)
            val messageLabelInterval = messageStringBuilder.appendText(messageLabel)
            messageStringBuilder.setSpan(ForegroundColorSpan(colorMessage), messageLabelInterval)
            listItem.post.shortText?.let {
                setUpVoteMessage(it)
            }
        }
        itemView.tvMessage.text = messageStringBuilder
    }

    private fun setAction(listItem: NotificationUpVoteItem, listItemEventsProcessor: NotificationsViewModelListEventsProcessor){
        val imageUrl = listItem.comment?.imageUrl ?: listItem.post?.imageUrl
        if(!imageUrl.isNullOrEmpty()){
            itemView.ivContent.loadNotificationImageContent(imageUrl)
            itemView.flAction.visibility = View.VISIBLE
            itemView.ivContent.visibility = View.VISIBLE
        } else{
            itemView.flAction.visibility = View.GONE
            itemView.ivContent.visibility = View.GONE
        }
        itemView.setOnClickListener{
            onItemClicked(listItem, listItemEventsProcessor)
        }
    }

    private fun onItemClicked(listItem: NotificationUpVoteItem, listItemEventsProcessor: NotificationsViewModelListEventsProcessor){
        val postContentId: ContentId? = (listItem.post?.contentId ?: listItem.comment?.parents?.post)?.mapToContentId()
        postContentId?.let {
            listItemEventsProcessor.onPostNavigateClicked(it)
        }
    }
}