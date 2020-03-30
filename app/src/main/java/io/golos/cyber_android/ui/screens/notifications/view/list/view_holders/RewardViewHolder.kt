package io.golos.cyber_android.ui.screens.notifications.view.list.view_holders

import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.screens.notifications.view.list.items.RewardNotificationItem
import io.golos.cyber_android.ui.screens.notifications.view_model.NotificationsViewModelListEventsProcessor
import io.golos.cyber_android.ui.shared.glide.loadAvatar
import io.golos.cyber_android.ui.shared.utils.getFormattedString
import io.golos.utils.format.CurrencyFormatter
import io.golos.utils.helpers.appendText
import io.golos.utils.helpers.setSpan
import kotlinx.android.synthetic.main.item_notification.view.*

class RewardViewHolder(
    parentView: ViewGroup
) : BaseNotificationViewHolder<RewardNotificationItem>(
    parentView
) {
    override val notificationTypeLabelResId: Int = R.drawable.ic_up_vote_label

    override fun setNotificationTypeLabel(widget: ImageView, listItem: RewardNotificationItem) {
        widget.setImageResource(R.drawable.ic_commun)
        widget.visibility = View.VISIBLE
    }

    override fun init(listItem: RewardNotificationItem, listItemEventsProcessor: NotificationsViewModelListEventsProcessor) {
        super.init(listItem, listItemEventsProcessor)
        setAction(listItem, listItemEventsProcessor)
        setMessage(listItem, listItemEventsProcessor)
    }

    override fun setUserAvatar(listItem: RewardNotificationItem, listItemEventsProcessor: NotificationsViewModelListEventsProcessor){
        if(listItem.communityAvatarUrl == null) {
            itemView.ivUserAvatar.setImageResource(R.drawable.ic_golden_coins)
        } else {
            itemView.ivUserAvatar.loadAvatar(listItem.communityAvatarUrl)
        }
    }

    private fun setAction(listItem: RewardNotificationItem, listItemEventsProcessor: NotificationsViewModelListEventsProcessor){
        itemView.flAction.visibility = View.GONE
        itemView.ivContent.visibility = View.GONE

        itemView.setOnClickListener {listItemEventsProcessor.onWalletNavigateClicked()}
    }

    private fun setMessage(listItem: RewardNotificationItem, listItemEventsProcessor: NotificationsViewModelListEventsProcessor) {
        val context = itemView.context
        val messageStringBuilder = SpannableStringBuilder()
        val colorMessage = ContextCompat.getColor(context, R.color.black)

        messageStringBuilder.append(context.resources.getString(R.string.you_have_got))
        messageStringBuilder.append(" ")
        messageStringBuilder.append(CurrencyFormatter.format(listItem.amount))
        messageStringBuilder.append(" ")
        messageStringBuilder.append(listItem.communityName)
        messageStringBuilder.append(" ")
        messageStringBuilder.append(context.resources.getString(R.string.points))

        messageStringBuilder.setSpan(ForegroundColorSpan(colorMessage), 0..messageStringBuilder.length)

        itemView.tvMessage.text = messageStringBuilder
    }
}