package io.golos.cyber_android.ui.screens.notifications.view.list.view_holders

import android.graphics.Typeface
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.screens.notifications.view.list.items.BaseNotificationItem
import io.golos.cyber_android.ui.screens.notifications.view_model.NotificationsViewModelListEventsProcessor
import io.golos.cyber_android.ui.shared.formatters.time_estimation.TimeEstimationFormatter
import io.golos.cyber_android.ui.shared.glide.loadAvatar
import io.golos.cyber_android.ui.shared.glide.release
import io.golos.cyber_android.ui.shared.recycler_view.ViewHolderBase
import io.golos.cyber_android.ui.shared.spans.ColorTextClickableSpan
import io.golos.domain.extensions.appendText
import io.golos.domain.extensions.setSpan
import io.golos.utils.SPACE
import kotlinx.android.synthetic.main.item_notification.view.*

abstract class BaseNotificationViewHolder<TItem: BaseNotificationItem> (
    parentView: ViewGroup
) : ViewHolderBase<NotificationsViewModelListEventsProcessor, TItem>(
    parentView,
    R.layout.item_notification
) {

    abstract val notificationTypeLabelResId: Int

    fun init(listItem: BaseNotificationItem, listItemEventsProcessor: NotificationsViewModelListEventsProcessor) {
        setUserAvatar(listItem, listItemEventsProcessor)
        setUnreadIndicatorVisibility(listItem)
        setCreateTime(listItem)
        setMessage(listItem, listItemEventsProcessor)
        setNotificationTypeLabel()
    }

    override fun release() {
        itemView.ivUserAvatar.release()
        super.release()
    }

    private fun setNotificationTypeLabel(){
        if(notificationTypeLabelResId == -1){
            itemView.ivNotificationTypeLabel.visibility = View.INVISIBLE
        } else{
            itemView.ivNotificationTypeLabel.visibility = View.VISIBLE
            itemView.ivNotificationTypeLabel.setImageResource(notificationTypeLabelResId)
        }
    }

    private fun setUserAvatar(listItem: BaseNotificationItem, listItemEventsProcessor: NotificationsViewModelListEventsProcessor){
        itemView.ivUserAvatar.loadAvatar(listItem.userAvatar)
        itemView.ivUserAvatar.setOnClickListener {
            val userId = listItem.userId
            listItemEventsProcessor.onUserClicked(userId)
        }
    }

    private fun setMessage(listItem: BaseNotificationItem, listItemEventsProcessor: NotificationsViewModelListEventsProcessor){
        val userId = listItem.userId
        val message = listItem.userName?.let {
            val result = SpannableStringBuilder()
            val userNameInterval = result.appendText(it)
            result.setSpan(StyleSpan(Typeface.BOLD), userNameInterval)
            val context = itemView.context
            val colorMessage = ContextCompat.getColor(context, R.color.black)
            result.setSpan(object : ColorTextClickableSpan(userId, colorMessage){

                override fun onClick(spanData: String) {
                    super.onClick(spanData)
                    listItemEventsProcessor.onUserClicked(spanData)
                }
            }, userNameInterval)
            result.append(SPACE)
            val messageInterval = result.appendText(context.getString(R.string.subscribed_to_you))
            result.setSpan(ForegroundColorSpan(colorMessage), messageInterval)
            result
        }
        itemView.tvMessage.text = message
    }

    private fun setCreateTime(listItem: BaseNotificationItem){
        val estimationTime = TimeEstimationFormatter(itemView.context).format(listItem.createTime)
        itemView.tvCreateTime.text = estimationTime
    }

    private fun setUnreadIndicatorVisibility(listItem: BaseNotificationItem){
        if(listItem.isNew){
            itemView.ivUnreadIndicator.visibility = View.VISIBLE
        } else{
            itemView.ivUnreadIndicator.visibility = View.INVISIBLE
        }
    }
}