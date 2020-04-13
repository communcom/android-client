package io.golos.cyber_android.ui.screens.notifications.view.list.view_holders.fill_logic

import android.graphics.Typeface
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import androidx.core.content.ContextCompat
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.screens.notifications.view.list.items.SubscribeNotificationItem
import io.golos.cyber_android.ui.screens.notifications.view_model.NotificationsViewModelListEventsProcessor
import io.golos.cyber_android.ui.shared.spans.ColorTextClickableSpan
import io.golos.domain.dto.UserIdDomain
import io.golos.utils.helpers.SPACE
import io.golos.utils.helpers.appendText
import io.golos.utils.helpers.setSpan

class NotificationViewFillSubscribe(viewDescription: NotificationView) : NotificationViewFillBase<SubscribeNotificationItem>(viewDescription) {
    override val notificationTypeLabelResId: Int = -1

    override fun init(listItem: SubscribeNotificationItem, listItemEventsProcessor: NotificationsViewModelListEventsProcessor) {
        super.init(listItem, listItemEventsProcessor)
        setMessage(listItem, listItemEventsProcessor)
        setAction(listItem, listItemEventsProcessor)
    }

    private fun setMessage(
        listItem: SubscribeNotificationItem,
        listItemEventsProcessor: NotificationsViewModelListEventsProcessor
    ) {
        val userId = listItem.userId

        val message = listItem.userName?.let {
            val result = SpannableStringBuilder()
            val userNameInterval = result.appendText(it)
            result.setSpan(StyleSpan(Typeface.BOLD), userNameInterval)
            val context = viewDescription.root.context
            val colorMessage = ContextCompat.getColor(context, R.color.black)
            result.setSpan(object : ColorTextClickableSpan(userId, colorMessage) {

                override fun onClick(spanData: String) {
                    super.onClick(spanData)
                    onItemClicked(listItem, listItemEventsProcessor)
                }
            }, userNameInterval)
            result.append(SPACE)
            val messageInterval = result.appendText(context.getString(R.string.subscribed_to_you))
            result.setSpan(ForegroundColorSpan(colorMessage), messageInterval)
            result
        }
        viewDescription.messageText.text = message
    }

    private fun setAction(listItem: SubscribeNotificationItem, listItemEventsProcessor: NotificationsViewModelListEventsProcessor){
        viewDescription.root.setOnClickListener{
            onItemClicked(listItem, listItemEventsProcessor)
        }
    }

    private fun onItemClicked(listItem: SubscribeNotificationItem, listItemEventsProcessor: NotificationsViewModelListEventsProcessor){
        listItemEventsProcessor.onUserClickedById(UserIdDomain(listItem.userId))
    }
}