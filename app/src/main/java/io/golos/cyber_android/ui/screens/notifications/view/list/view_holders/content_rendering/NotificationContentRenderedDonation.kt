package io.golos.cyber_android.ui.screens.notifications.view.list.view_holders.content_rendering

import android.graphics.Typeface
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.View
import android.widget.ImageView
import androidx.core.content.ContextCompat
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.screens.notifications.view.list.items.DonationNotificationItem
import io.golos.cyber_android.ui.screens.notifications.view.list.view_holders.content_rendering.view.NotificationView
import io.golos.cyber_android.ui.screens.notifications.view_model.NotificationsViewModelListEventsProcessor
import io.golos.cyber_android.ui.shared.glide.loadCommunity
import io.golos.cyber_android.ui.shared.spans.ColorTextClickableSpan
import io.golos.cyber_android.ui.shared.utils.getFormattedString
import io.golos.domain.GlobalConstants
import io.golos.domain.dto.UserIdDomain
import io.golos.utils.format.CurrencyFormatter
import io.golos.utils.helpers.SPACE
import io.golos.utils.helpers.appendText
import io.golos.utils.helpers.setSpan

class NotificationContentRenderedDonation(viewDescription: NotificationView) : NotificationContentRenderedBase<DonationNotificationItem>(viewDescription) {
    override fun init(listItem: DonationNotificationItem, listItemEventsProcessor: NotificationsViewModelListEventsProcessor) {
        super.init(listItem, listItemEventsProcessor)
        setAction(listItem, listItemEventsProcessor)
        setMessage(listItem, listItemEventsProcessor)
    }

    override fun setNotificationTypeLabel(widget: ImageView, listItem: DonationNotificationItem) {
        if(listItem.communityId.code == GlobalConstants.COMMUN_CODE || listItem.communityAvatarUrl == null) {
            widget.setImageResource(R.drawable.ic_commun)
        } else {
            widget.loadCommunity(listItem.communityAvatarUrl)
        }
        widget.visibility = View.VISIBLE
    }

    private fun setAction(listItem: DonationNotificationItem, listItemEventsProcessor: NotificationsViewModelListEventsProcessor){
        viewDescription.followButton.visibility = View.GONE
        viewDescription.contentIcon.visibility = View.GONE

        viewDescription.root.setOnClickListener {listItemEventsProcessor.onWalletNavigateClicked()}
    }

    private fun setMessage(listItem: DonationNotificationItem, listItemEventsProcessor: NotificationsViewModelListEventsProcessor) {
        val context = viewDescription.root.context
        val messageStringBuilder = SpannableStringBuilder()
        val userName = listItem.userName
        val userId = listItem.userId
        val colorMessage = ContextCompat.getColor(context, R.color.black)
        val colorAmount = ContextCompat.getColor(context, R.color.blue)

        // User name
        userName?.let {
            val userNameInterval = messageStringBuilder.appendText(it)
            messageStringBuilder.setSpan(StyleSpan(Typeface.BOLD), userNameInterval)
            messageStringBuilder.setSpan(object : ColorTextClickableSpan(userId, colorMessage) {
                override fun onClick(spanData: String) {
                    super.onClick(spanData)
                    listItemEventsProcessor.onUserClickedById(UserIdDomain(userId))
                }
            }, userNameInterval)
            messageStringBuilder.append(SPACE)
        }

        // Main text
        val mainText = context.resources.getFormattedString(R.string.donate_your_post, listItem.postTextBrief ?: "")
        val mainTextInterval = messageStringBuilder.appendText(mainText)
        messageStringBuilder.setSpan(ForegroundColorSpan(colorMessage), mainTextInterval)

        messageStringBuilder.append(SPACE)

        // Tail
        val tailText = "${CurrencyFormatter.format(listItem.amount)} ${listItem.communityName} ${context.getString(R.string.points)}"
        val tailTextInterval = messageStringBuilder.appendText(tailText)
        messageStringBuilder.setSpan(ForegroundColorSpan(colorAmount), tailTextInterval)
        messageStringBuilder.setSpan(StyleSpan(Typeface.BOLD), tailTextInterval)

        viewDescription.messageText.text = messageStringBuilder
    }
}