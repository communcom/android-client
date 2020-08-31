package io.golos.cyber_android.ui.screens.notifications.view.list.view_holders.content_rendering

import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.ImageView
import androidx.core.content.ContextCompat
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.screens.notifications.view.list.items.ReferralPurchaseBonusNotificationItem
import io.golos.cyber_android.ui.screens.notifications.view.list.view_holders.content_rendering.view.NotificationView
import io.golos.cyber_android.ui.screens.notifications.view_model.NotificationsViewModelListEventsProcessor
import io.golos.utils.format.CurrencyFormatter
import io.golos.utils.helpers.appendSpannedText
import io.golos.utils.helpers.capitalize
import androidx.appcompat.app.AppCompatDelegate

class NotificationContentRendererReferralPurchaseBonus(
    viewDescription: NotificationView
) : NotificationContentRenderedBase<ReferralPurchaseBonusNotificationItem>(viewDescription) {
    override fun init(listItem: ReferralPurchaseBonusNotificationItem, listItemEventsProcessor: NotificationsViewModelListEventsProcessor) {
        super.init(listItem, listItemEventsProcessor)
        setAction(listItem, listItemEventsProcessor)
        setMessage(listItem, listItemEventsProcessor)
    }

    override fun setNotificationTypeLabel(widget: ImageView, listItem: ReferralPurchaseBonusNotificationItem) {
        widget.setImageResource(R.drawable.ic_commun)
        widget.visibility = View.INVISIBLE
    }

    override fun setUserAvatar(listItem: ReferralPurchaseBonusNotificationItem){
        viewDescription.userIcon.setImageResource(R.drawable.ic_golden_coins)
    }

    private fun setAction(listItem: ReferralPurchaseBonusNotificationItem, listItemEventsProcessor: NotificationsViewModelListEventsProcessor){
        viewDescription.followButton.visibility = View.GONE
        viewDescription.contentIcon.visibility = View.GONE

        viewDescription.root.setOnClickListener {listItemEventsProcessor.onWalletNavigateClicked()}
    }

    private fun setMessage(listItem: ReferralPurchaseBonusNotificationItem, listItemEventsProcessor: NotificationsViewModelListEventsProcessor) {
        val context = viewDescription.root.context
        val messageStringBuilder = SpannableStringBuilder()

        val colorMessage = if(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES)
            R.color.black_dark_theme
        else R.color.black
        val colorUser = R.color.blue

        messageStringBuilder.appendSpannedText(context.resources.getString(R.string.referral_purchase1), createColorSpan(context, colorMessage))
        messageStringBuilder.append(" ")
        messageStringBuilder.appendSpannedText(CurrencyFormatter.format(listItem.amount), createColorSpan(context, colorMessage))
        messageStringBuilder.append(" ")
        messageStringBuilder.appendSpannedText(context.resources.getString(R.string.commun).capitalize(), createColorSpan(context, colorMessage))
        messageStringBuilder.appendSpannedText(". ", createColorSpan(context, colorMessage))
        messageStringBuilder.appendSpannedText(context.resources.getString(R.string.referral_purchase2), createColorSpan(context, colorMessage))
        messageStringBuilder.appendSpannedText(" ", createColorSpan(context, colorMessage))
        messageStringBuilder.appendSpannedText(listItem.percent.toString(), createColorSpan(context, colorMessage))
        messageStringBuilder.appendSpannedText(context.resources.getString(R.string.referral_purchase3), createColorSpan(context, colorMessage))
        messageStringBuilder.appendSpannedText(" ", createColorSpan(context, colorMessage))
        messageStringBuilder.appendSpannedText(listItem.referral.name ?: listItem.referral.id.userId, createColorSpan(context, colorUser))
        messageStringBuilder.appendSpannedText(context.resources.getString(R.string.referral_purchase4), createColorSpan(context, colorMessage))

        viewDescription.messageText.text = messageStringBuilder
    }
}