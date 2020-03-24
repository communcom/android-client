package io.golos.cyber_android.ui.screens.notifications.view.list.view_holders

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.screens.notifications.view.list.items.BaseNotificationItem
import io.golos.cyber_android.ui.screens.notifications.view_model.NotificationsViewModelListEventsProcessor
import io.golos.utils.format.TimeEstimationFormatter
import io.golos.cyber_android.ui.shared.glide.clear
import io.golos.cyber_android.ui.shared.glide.loadAvatar
import io.golos.cyber_android.ui.shared.recycler_view.ViewHolderBase
import io.golos.cyber_android.ui.shared.utils.adjustSpannableClicks
import io.golos.domain.GlobalConstants
import io.golos.domain.dto.UserIdDomain
import kotlinx.android.synthetic.main.item_notification.view.*

abstract class BaseNotificationViewHolder<TItem: BaseNotificationItem> (
    parentView: ViewGroup
) : ViewHolderBase<NotificationsViewModelListEventsProcessor, TItem>(
    parentView,
    R.layout.item_notification
) {

    protected open val notificationTypeLabelResId: Int = -1

    override fun init(listItem: TItem, listItemEventsProcessor: NotificationsViewModelListEventsProcessor) {
        itemView.tvMessage.adjustSpannableClicks()
        setUserAvatar(listItem, listItemEventsProcessor)
        setUnreadIndicatorVisibility(listItem)
        setCreateTime(listItem)
        setNotificationTypeLabel(itemView.ivNotificationTypeLabel, listItem)
    }

    override fun release() {
        itemView.ivUserAvatar.clear()
        itemView.ivContent.clear()
        itemView.ivNotificationTypeLabel.clear()
        super.release()
    }

    protected open fun setNotificationTypeLabel(widget: ImageView, listItem: TItem) {
        if(notificationTypeLabelResId == -1){
            widget.visibility = View.INVISIBLE
        } else{
            widget.visibility = View.VISIBLE
            widget.setImageResource(notificationTypeLabelResId)
        }
    }

    private fun setUserAvatar(listItem: BaseNotificationItem, listItemEventsProcessor: NotificationsViewModelListEventsProcessor){
        if(listItem.userId == GlobalConstants.C_BOUNTY_USER_ID) {
            itemView.ivUserAvatar.setImageResource(R.drawable.ic_golden_coins)
        } else {
            itemView.ivUserAvatar.loadAvatar(listItem.userAvatar)
        }

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
        val estimationTime = TimeEstimationFormatter.format(listItem.createTime, itemView.context)
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