package io.golos.cyber_android.ui.screens.notifications.view.list.view_holders.content_rendering

import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.ImageView
import androidx.core.content.ContextCompat
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.screens.notifications.view.list.items.ReferralRegistrationBonusNotificationItem
import io.golos.cyber_android.ui.screens.notifications.view.list.view_holders.content_rendering.view.NotificationView
import io.golos.cyber_android.ui.screens.notifications.view_model.NotificationsViewModelListEventsProcessor
import io.golos.utils.format.CurrencyFormatter
import io.golos.utils.helpers.appendSpannedText
import io.golos.utils.helpers.capitalize
import androidx.appcompat.app.AppCompatDelegate

class NotificationContentRendererReferralRegistrationBonus(
    viewDescription: NotificationView
) : NotificationContentRenderedBase<ReferralRegistrationBonusNotificationItem>(viewDescription) {
    override fun init(listItem: ReferralRegistrationBonusNotificationItem, listItemEventsProcessor: NotificationsViewModelListEventsProcessor) {
        super.init(listItem, listItemEventsProcessor)
        setAction(listItem, listItemEventsProcessor)
        setMessage(listItem, listItemEventsProcessor)
    }

    override fun setNotificationTypeLabel(widget: ImageView, listItem: ReferralRegistrationBonusNotificationItem) {
        widget.setImageResource(R.drawable.ic_commun)
        widget.visibility = View.INVISIBLE
    }

    override fun setUserAvatar(listItem: ReferralRegistrationBonusNotificationItem){
        viewDescription.userIcon.setImageResource(R.drawable.ic_golden_coins)
    }

    private fun setAction(listItem: ReferralRegistrationBonusNotificationItem, listItemEventsProcessor: NotificationsViewModelListEventsProcessor){
        viewDescription.followButton.visibility = View.GONE
        viewDescription.contentIcon.visibility = View.GONE

        viewDescription.root.setOnClickListener {listItemEventsProcessor.onWalletNavigateClicked()}
    }

    private fun setMessage(listItem: ReferralRegistrationBonusNotificationItem, listItemEventsProcessor: NotificationsViewModelListEventsProcessor) {
        val context = viewDescription.root.context
        val messageStringBuilder = SpannableStringBuilder()

        val colorMessage =if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES)
            R.color.black_dark_theme
        else
            R.color.black
        val colorUser = R.color.blue

        messageStringBuilder.appendSpannedText(context.resources.getString(R.string.referral_registration), createColorSpan(context, colorMessage))
        messageStringBuilder.append(" ")
        messageStringBuilder.appendSpannedText(listItem.referral.name ?: listItem.referral.id.userId, createColorSpan(context, colorUser))
        messageStringBuilder.append(" ")
        messageStringBuilder.appendSpannedText(CurrencyFormatter.format(listItem.amount), createColorSpan(context, colorMessage))
        messageStringBuilder.append(" ")
        messageStringBuilder.appendSpannedText(context.resources.getString(R.string.commun).capitalize(), createColorSpan(context, colorMessage))

        viewDescription.messageText.text = messageStringBuilder
    }
}