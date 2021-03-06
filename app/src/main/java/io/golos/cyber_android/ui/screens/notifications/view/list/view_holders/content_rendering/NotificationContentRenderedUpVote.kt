package io.golos.cyber_android.ui.screens.notifications.view.list.view_holders.content_rendering

import android.graphics.Typeface
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.View
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.mappers.mapToContentId
import io.golos.cyber_android.ui.screens.notifications.view.list.items.UpVoteNotificationItem
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

class NotificationContentRenderedUpVote(viewDescription: NotificationView) : NotificationContentRenderedBase<UpVoteNotificationItem>(viewDescription) {
    override val notificationTypeLabelResId: Int = R.drawable.ic_up_vote_label

    override fun init(listItem: UpVoteNotificationItem, listItemEventsProcessor: NotificationsViewModelListEventsProcessor) {
        super.init(listItem, listItemEventsProcessor)
        setAction(listItem, listItemEventsProcessor)
        setMessage(listItem, listItemEventsProcessor)
    }

    private fun setMessage(listItem: UpVoteNotificationItem, listItemEventsProcessor: NotificationsViewModelListEventsProcessor) {
        val context = viewDescription.root.context
        val messageStringBuilder = SpannableStringBuilder()
        val userName = listItem.userName
        val userId = listItem.userId
        val colorMessage = getStyledAttribute(R.attr.black, context)

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
        viewDescription.messageText.text = messageStringBuilder
    }

    private fun setAction(listItem: UpVoteNotificationItem, listItemEventsProcessor: NotificationsViewModelListEventsProcessor){
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

    private fun onItemClicked(listItem: UpVoteNotificationItem, listItemEventsProcessor: NotificationsViewModelListEventsProcessor){
        val postContentId: ContentIdDomain? = (listItem.post?.contentId ?: listItem.comment?.parents?.post)?.mapToContentId()
        postContentId?.let {
            listItemEventsProcessor.onPostNavigateClicked(it)
        }
    }
}