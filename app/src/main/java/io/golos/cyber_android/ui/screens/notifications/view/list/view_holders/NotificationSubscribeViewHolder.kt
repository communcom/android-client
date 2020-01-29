package io.golos.cyber_android.ui.screens.notifications.view.list.view_holders

import android.graphics.Typeface
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.screens.notifications.view.list.items.NotificationSubscribeItem
import io.golos.cyber_android.ui.screens.notifications.view_model.NotificationsViewModelListEventsProcessor
import io.golos.cyber_android.ui.shared.spans.ColorTextClickableSpan
import io.golos.domain.dto.UserIdDomain
import io.golos.domain.extensions.appendText
import io.golos.domain.extensions.setSpan
import io.golos.utils.SPACE
import kotlinx.android.synthetic.main.item_notification.view.*

class NotificationSubscribeViewHolder(
    parentView: ViewGroup
) : BaseNotificationViewHolder<NotificationSubscribeItem>(
    parentView
) {

    override val notificationTypeLabelResId: Int = -1

    override fun init(listItem: NotificationSubscribeItem, listItemEventsProcessor: NotificationsViewModelListEventsProcessor) {
        super.init(listItem, listItemEventsProcessor)
        setMessage(listItem, listItemEventsProcessor)
        setAction(listItem, listItemEventsProcessor)
    }

    private fun setMessage(
        listItem: NotificationSubscribeItem,
        listItemEventsProcessor: NotificationsViewModelListEventsProcessor
    ) {
        val userId = listItem.userId
        val message = listItem.userName?.let {
            val result = SpannableStringBuilder()
            val userNameInterval = result.appendText(it)
            result.setSpan(StyleSpan(Typeface.BOLD), userNameInterval)
            val context = itemView.context
            val colorMessage = ContextCompat.getColor(context, R.color.black)
            result.setSpan(object : ColorTextClickableSpan(userId, colorMessage) {

                override fun onClick(spanData: String) {
                    super.onClick(spanData)
                    listItemEventsProcessor.onUserClickedById(UserIdDomain(spanData))
                }
            }, userNameInterval)
            result.append(SPACE)
            val messageInterval = result.appendText(context.getString(R.string.subscribed_to_you))
            result.setSpan(ForegroundColorSpan(colorMessage), messageInterval)
            result
        }
        itemView.tvMessage.text = message
    }

    private fun setAction(listItem: NotificationSubscribeItem, listItemEventsProcessor: NotificationsViewModelListEventsProcessor){
        itemView.setOnClickListener {
            listItemEventsProcessor.onUserClickedById(UserIdDomain(listItem.userId))
        }
    }
}