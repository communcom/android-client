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
import io.golos.cyber_android.ui.screens.notifications.view.list.items.NotificationReplyItem
import io.golos.cyber_android.ui.screens.notifications.view_model.NotificationsViewModelListEventsProcessor
import io.golos.cyber_android.ui.shared.glide.loadNotificationImageContent
import io.golos.cyber_android.ui.shared.spans.ColorTextClickableSpan
import io.golos.domain.dto.UserIdDomain
import io.golos.domain.extensions.appendText
import io.golos.domain.extensions.setSpan
import io.golos.utils.SPACE
import kotlinx.android.synthetic.main.item_notification.view.*

class NotificationReplyViewHolder(
    parentView: ViewGroup
) : BaseNotificationViewHolder<NotificationReplyItem>(
    parentView
) {

    override val notificationTypeLabelResId: Int = R.drawable.ic_reply_label

    override fun init(listItem: NotificationReplyItem, listItemEventsProcessor: NotificationsViewModelListEventsProcessor) {
        super.init(listItem, listItemEventsProcessor)
        setAction(listItem, listItemEventsProcessor)
        setMessage(listItem, listItemEventsProcessor)
    }

    private fun setMessage(listItem: NotificationReplyItem, listItemEventsProcessor: NotificationsViewModelListEventsProcessor){
        val resources = itemView.context.resources
        val context = itemView.context
        val messageStringBuilder = SpannableStringBuilder()
        val userName = listItem.userName
        val userId = listItem.userId
        val colorMessage = ContextCompat.getColor(context, R.color.black)
        val colorCurrentUserName =  ContextCompat.getColor(context, R.color.blue)
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
        val currentUserName = "@" + listItem.currentUserName

        fun setCurrentUserName(){
            val currentUserNameInterval = messageStringBuilder.appendText(currentUserName)
            messageStringBuilder.setSpan(ForegroundColorSpan(colorCurrentUserName), currentUserNameInterval)
        }

        fun setReplyMessage(message: String){
            if(message.contains(currentUserName)){
                val split = message.split(currentUserName).filter { it.isNotEmpty() }
                val firstQuotesInterval = messageStringBuilder.appendText("''")
                messageStringBuilder.setSpan(ForegroundColorSpan(colorMessage), firstQuotesInterval)
                if(message.startsWith(currentUserName)){
                    setCurrentUserName()
                }
                split.forEachIndexed{ index, partString ->
                    val partStringInterval = messageStringBuilder.appendText(partString)
                    messageStringBuilder.setSpan(ForegroundColorSpan(colorMessage), partStringInterval)
                    if(index == split.lastIndex && message.endsWith(currentUserName) || index != split.lastIndex){
                        setCurrentUserName()
                    }
                }
                val lastQuotesInterval = messageStringBuilder.appendText("''")
                messageStringBuilder.setSpan(ForegroundColorSpan(colorMessage), lastQuotesInterval)
            } else{
                val commentInterval = messageStringBuilder.appendText("''$message''")
                messageStringBuilder.setSpan(ForegroundColorSpan(colorMessage), commentInterval)
            }
        }

        val mentionLabel = resources.getString(R.string.left_comment)
        val labelInterval = messageStringBuilder.appendText(mentionLabel)
        messageStringBuilder.setSpan(ForegroundColorSpan(colorMessage), labelInterval)
        messageStringBuilder.appendText(SPACE)
        listItem.comment.shortText?.let {
            setReplyMessage(it)
        }
        itemView.tvMessage.text = messageStringBuilder
    }

    private fun setAction(listItem: NotificationReplyItem, listItemEventsProcessor: NotificationsViewModelListEventsProcessor){
        val imageUrl = listItem.comment.imageUrl
        if(!imageUrl.isNullOrEmpty()){
            itemView.ivContent.loadNotificationImageContent(imageUrl)
            itemView.flAction.visibility = View.VISIBLE
            itemView.ivContent.visibility = View.VISIBLE
        } else{
            itemView.flAction.visibility = View.GONE
            itemView.ivContent.visibility = View.GONE
        }
        itemView.setOnClickListener {
            val postContentId: ContentId = listItem.comment.parents.post.mapToContentId()
            listItemEventsProcessor.onPostNavigateClicked(postContentId)
        }
    }
}