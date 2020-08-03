package io.golos.cyber_android.ui.screens.notifications.view.list.view_holders.content_rendering

import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.screens.notifications.view.list.items.RewardNotificationItem
import io.golos.cyber_android.ui.screens.notifications.view.list.view_holders.content_rendering.view.NotificationView
import io.golos.cyber_android.ui.screens.notifications.view_model.NotificationsViewModelListEventsProcessor
import io.golos.cyber_android.ui.shared.glide.loadAvatar
import io.golos.domain.GlobalConstants
import io.golos.utils.format.CurrencyFormatter
import io.golos.utils.helpers.setSpan

class NotificationContentRenderedReward(viewDescription: NotificationView) : NotificationContentRenderedBase<RewardNotificationItem>(viewDescription) {
    override val notificationTypeLabelResId: Int = R.drawable.ic_up_vote_label

    override fun init(listItem: RewardNotificationItem, listItemEventsProcessor: NotificationsViewModelListEventsProcessor) {
        super.init(listItem, listItemEventsProcessor)
        setAction(listItem, listItemEventsProcessor)
        setMessage(listItem, listItemEventsProcessor)
    }

    override fun setNotificationTypeLabel(widget: ImageView, listItem: RewardNotificationItem) {
        widget.setImageResource(R.drawable.ic_commun)
        widget.visibility = View.VISIBLE
    }

    override fun setUserAvatar(listItem: RewardNotificationItem){
        if(listItem.communityAvatarUrl == null) {
            viewDescription.userIcon.setImageResource(R.drawable.ic_golden_coins)
        } else {
            viewDescription.userIcon.loadAvatar(listItem.communityAvatarUrl)
        }
    }

    private fun setAction(listItem: RewardNotificationItem, listItemEventsProcessor: NotificationsViewModelListEventsProcessor){
        viewDescription.followButton.visibility = View.GONE
        viewDescription.contentIcon.visibility = View.GONE

        viewDescription.root.setOnClickListener {listItemEventsProcessor.onWalletNavigateClicked()}
    }

    private fun setMessage(listItem: RewardNotificationItem, listItemEventsProcessor: NotificationsViewModelListEventsProcessor) {
        val context = viewDescription.root.context
        val messageStringBuilder = SpannableStringBuilder()
        val colorMessage = if(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES)
                ContextCompat.getColor(context,R.color.black_dark_theme)
            else ContextCompat.getColor(context,R.color.black)

        messageStringBuilder.append(context.resources.getString(R.string.you_have_got))
        messageStringBuilder.append(" ")
        messageStringBuilder.append(CurrencyFormatter.format(listItem.amount))
        if(listItem.communityName != GlobalConstants.COMMUN_CODE) {
            messageStringBuilder.append(" ")
            messageStringBuilder.append(context.resources.getQuantityText(R.plurals.points_text,listItem.amount.toInt()))
        }
        messageStringBuilder.append(" ")
        messageStringBuilder.append(listItem.communityName)

        messageStringBuilder.setSpan(ForegroundColorSpan(colorMessage), 0..messageStringBuilder.length)

        viewDescription.messageText.text = messageStringBuilder
    }
}