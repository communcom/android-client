package io.golos.cyber_android.ui.screens.notifications.view.list.view_holders.content_rendering

import android.graphics.Typeface
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.View
import android.widget.ImageView
import androidx.core.content.ContextCompat
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.screens.notifications.view.list.items.TransferNotificationItem
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

class NotificationContentRenderedTransfer(viewDescription: NotificationView) : NotificationContentRenderedBase<TransferNotificationItem>(viewDescription) {
    override fun init(listItem: TransferNotificationItem, listItemEventsProcessor: NotificationsViewModelListEventsProcessor) {
        super.init(listItem, listItemEventsProcessor)
        setAction(listItem, listItemEventsProcessor)
        setMessage(listItem, listItemEventsProcessor)
    }

    override fun setNotificationTypeLabel(widget: ImageView, listItem: TransferNotificationItem) {
        if(listItem.communityId.code == GlobalConstants.COMMUN_CODE || listItem.communityAvatarUrl == null) {
            widget.setImageResource(R.drawable.ic_commun)
        } else {
            widget.loadCommunity(listItem.communityAvatarUrl)
        }
    }

    private fun setAction(listItem: TransferNotificationItem, listItemEventsProcessor: NotificationsViewModelListEventsProcessor){
        viewDescription.followButton.visibility = View.GONE
        viewDescription.contentIcon.visibility = View.GONE

        viewDescription.root.setOnClickListener {listItemEventsProcessor.onWalletNavigateClicked()}
    }

    private fun setMessage(listItem: TransferNotificationItem, listItemEventsProcessor: NotificationsViewModelListEventsProcessor) {
        val context = viewDescription.root.context
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
                    listItemEventsProcessor.onUserClickedById(UserIdDomain(userId))
                }
            }, userNameInterval)
            messageStringBuilder.append(SPACE)
        }

        val tail = context.resources.getFormattedString(
            R.string.sent_you,
            CurrencyFormatter.format(listItem.amount),
            listItem.communityName)

        val tailInterval = messageStringBuilder.appendText(tail)
        messageStringBuilder.setSpan(ForegroundColorSpan(colorMessage), tailInterval)

        viewDescription.messageText.text = messageStringBuilder
    }
}