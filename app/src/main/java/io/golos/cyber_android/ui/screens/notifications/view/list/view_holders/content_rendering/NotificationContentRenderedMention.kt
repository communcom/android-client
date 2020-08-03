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
import io.golos.cyber_android.ui.screens.notifications.view.list.items.MentionNotificationItem
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

class NotificationContentRenderedMention(viewDescription: NotificationView) : NotificationContentRenderedBase<MentionNotificationItem>(viewDescription) {
    override val notificationTypeLabelResId: Int = R.drawable.ic_mention_label

    override fun init(listItem: MentionNotificationItem, listItemEventsProcessor: NotificationsViewModelListEventsProcessor) {
        super.init(listItem, listItemEventsProcessor)

        setAction(listItem, listItemEventsProcessor)
        setMessage(listItem, listItemEventsProcessor)
    }

    private fun setMessage(listItem: MentionNotificationItem, listItemEventsProcessor: NotificationsViewModelListEventsProcessor){
        val resources = viewDescription.root.context.resources
        val context = viewDescription.root.context

        val messageStringBuilder = SpannableStringBuilder()
        val userName = listItem.userName
        val userId = listItem.userId
        val colorMessage = if(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES)
                ContextCompat.getColor(context,R.color.black_dark_theme)
            else ContextCompat.getColor(context,R.color.black)
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

        fun setMentionMessage(message: String){
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

        if(listItem.comment != null){
            val mentionLabel = resources.getString(R.string.mention_on_comment)
            val labelInterval = messageStringBuilder.appendText(mentionLabel)
            messageStringBuilder.setSpan(ForegroundColorSpan(colorMessage), labelInterval)
            messageStringBuilder.appendText(SPACE)
            listItem.comment.shortText?.let {
                setMentionMessage(it)
            }

        } else if(listItem.post != null){
            val mentionLabel = resources.getString(R.string.mention_on_post)
            val labelInterval = messageStringBuilder.appendText(mentionLabel)
            messageStringBuilder.setSpan(ForegroundColorSpan(colorMessage), labelInterval)
            messageStringBuilder.appendText(SPACE)
            listItem.post.shortText?.let {
                setMentionMessage(it)
            }
        }
        viewDescription.messageText.text = messageStringBuilder
    }

    private fun setAction(listItem: MentionNotificationItem, listItemEventsProcessor: NotificationsViewModelListEventsProcessor){
        val imageUrl = listItem.comment?.imageUrl ?: listItem.post?.imageUrl
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

    private fun onItemClicked(listItem: MentionNotificationItem, listItemEventsProcessor: NotificationsViewModelListEventsProcessor){
        val postContentId: ContentIdDomain? = (listItem.post?.contentId ?: listItem.comment?.parents?.post)?.mapToContentId()
        postContentId?.let {
            listItemEventsProcessor.onPostNavigateClicked(it)
        }
    }

}