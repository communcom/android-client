package io.golos.cyber_android.ui.screens.notifications.view.list.view_holders

import android.view.View
import android.view.ViewGroup
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.screens.notifications.view.list.items.BaseNotificationItem
import io.golos.cyber_android.ui.screens.notifications.view_model.NotificationsViewModelListEventsProcessor
import io.golos.cyber_android.ui.shared.formatters.time_estimation.TimeEstimationFormatter
import io.golos.cyber_android.ui.shared.glide.clear
import io.golos.cyber_android.ui.shared.glide.loadAvatar
import io.golos.cyber_android.ui.shared.recycler_view.ViewHolderBase
import io.golos.cyber_android.ui.shared.utils.adjustSpannableClicks
import io.golos.domain.dto.UserIdDomain
import kotlinx.android.synthetic.main.item_notification.view.*

abstract class BaseNotificationViewHolder<TItem: BaseNotificationItem> (
    parentView: ViewGroup
) : ViewHolderBase<NotificationsViewModelListEventsProcessor, TItem>(
    parentView,
    R.layout.item_notification
) {

    abstract val notificationTypeLabelResId: Int

    fun init(listItem: BaseNotificationItem, listItemEventsProcessor: NotificationsViewModelListEventsProcessor) {
        itemView.tvMessage.adjustSpannableClicks()
        setUserAvatar(listItem, listItemEventsProcessor)
        setUnreadIndicatorVisibility(listItem)
        setCreateTime(listItem)
        setNotificationTypeLabel()
    }

    override fun release() {
        itemView.ivUserAvatar.clear()
        itemView.ivContent.clear()
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
            listItemEventsProcessor.onUserClickedById(UserIdDomain(userId))
        }
        itemView.ivNotificationTypeLabel.setOnClickListener {
            val userId = listItem.userId
            listItemEventsProcessor.onUserClickedById(UserIdDomain(userId))
        }
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