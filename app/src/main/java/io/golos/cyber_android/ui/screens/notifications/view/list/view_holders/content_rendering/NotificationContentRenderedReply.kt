package io.golos.cyber_android.ui.screens.notifications.view.list.view_holders.content_rendering

import android.graphics.Typeface
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.mappers.mapToContentId
import io.golos.cyber_android.ui.screens.notifications.view.list.items.ReplyNotificationItem
import io.golos.cyber_android.ui.screens.notifications.view.list.view_holders.content_rendering.view.NotificationView
import io.golos.cyber_android.ui.screens.notifications.view_model.NotificationsViewModelListEventsProcessor
import io.golos.cyber_android.ui.shared.glide.loadNotificationImageContent
import io.golos.cyber_android.ui.shared.spans.ColorTextClickableSpan
import io.golos.cyber_android.ui.shared.utils.getStyledAttribute
import io.golos.domain.dto.ContentIdDomain
import io.golos.domain.dto.UserIdDomain
import io.golos.utils.helpers.SPACE
import io.golos.utils.helpers.appendText
import io.golos.utils.helpers.setSpan

class NotificationContentRenderedReply(viewDescription: NotificationView) : NotificationContentRenderedBase<ReplyNotificationItem>(viewDescription) {
    override val notificationTypeLabelResId: Int = R.drawable.ic_reply_label

    override fun init(listItem: ReplyNotificationItem, listItemEventsProcessor: NotificationsViewModelListEventsProcessor) {
        super.init(listItem, listItemEventsProcessor)
        setAction(listItem, listItemEventsProcessor)
        setMessage(listItem, listItemEventsProcessor)
    }

    private fun setMessage(listItem: ReplyNotificationItem, listItemEventsProcessor: NotificationsViewModelListEventsProcessor){
        val resources = viewDescription.root.context.resources
        val context = viewDescription.root.context
        val messageStringBuilder = SpannableStringBuilder()
        val userName = listItem.userName
        val userId = listItem.userId
        val colorMessage = getStyledAttribute(R.attr.black, context)
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
        viewDescription.messageText.text = messageStringBuilder
    }

    private fun setAction(listItem: ReplyNotificationItem, listItemEventsProcessor: NotificationsViewModelListEventsProcessor){
        val imageUrl = listItem.comment.imageUrl
        if(!imageUrl.isNullOrEmpty()){
            viewDescription.contentIcon.loadNotificationImageContent(imageUrl)
            viewDescription.followButton.visibility = View.VISIBLE
            viewDescription.contentIcon.visibility = View.VISIBLE
        } else{
            viewDescription.followButton.visibility = View.GONE
            viewDescription.contentIcon.visibility = View.GONE
        }
        viewDescription.root.setOnClickListener{
            onItemClicked(listItem, listItemEventsProcessor)
        }
    }

    private fun onItemClicked(listItem: ReplyNotificationItem, listItemEventsProcessor: NotificationsViewModelListEventsProcessor){
        val postContentId: ContentIdDomain = listItem.comment.parents.post.mapToContentId()
        listItemEventsProcessor.onPostNavigateClicked(postContentId)
    }
}