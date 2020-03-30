package io.golos.cyber_android.ui.screens.notifications.view.list.view_holders

import android.graphics.Typeface
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.screens.notifications.view.list.items.TransferNotificationItem
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
import kotlinx.android.synthetic.main.item_notification.view.*

class TransferViewHolder(
    parentView: ViewGroup
) : BaseNotificationViewHolder<TransferNotificationItem>(
    parentView
) {
    override fun setNotificationTypeLabel(widget: ImageView, listItem: TransferNotificationItem) {
        if(listItem.communityId.code == GlobalConstants.COMMUN_CODE || listItem.communityAvatarUrl == null) {
            widget.setImageResource(R.drawable.ic_commun)
        } else {
            widget.loadCommunity(listItem.communityAvatarUrl)
        }
    }

    override fun init(listItem: TransferNotificationItem, listItemEventsProcessor: NotificationsViewModelListEventsProcessor) {
        super.init(listItem, listItemEventsProcessor)
        setAction(listItem, listItemEventsProcessor)
        setMessage(listItem, listItemEventsProcessor)
    }

    private fun setAction(listItem: TransferNotificationItem, listItemEventsProcessor: NotificationsViewModelListEventsProcessor){
        itemView.flAction.visibility = View.GONE
        itemView.ivContent.visibility = View.GONE

        itemView.setOnClickListener {listItemEventsProcessor.onWalletNavigateClicked()}
    }

    private fun setMessage(listItem: TransferNotificationItem, listItemEventsProcessor: NotificationsViewModelListEventsProcessor) {
        val context = itemView.context
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

        itemView.tvMessage.text = messageStringBuilder
    }
}