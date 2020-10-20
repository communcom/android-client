package io.golos.cyber_android.ui.screens.notifications.view.list.view_holders.content_rendering

import android.content.Context
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.ImageView
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.screens.notifications.view.list.items.BaseNotificationItem
import io.golos.cyber_android.ui.screens.notifications.view.list.view_holders.content_rendering.view.NotificationView
import io.golos.cyber_android.ui.screens.notifications.view_model.NotificationsViewModelListEventsProcessor
import io.golos.cyber_android.ui.shared.glide.clear
import io.golos.cyber_android.ui.shared.glide.loadAvatar
import io.golos.cyber_android.ui.shared.utils.adjustSpannableClicks
import io.golos.domain.GlobalConstants
import io.golos.domain.dto.UserIdDomain
import io.golos.utils.format.TimeEstimationFormatter

abstract class NotificationContentRenderedBase<TItem: BaseNotificationItem>(protected val viewDescription: NotificationView) {
    protected open val notificationTypeLabelResId: Int = -1

    open fun init(listItem: TItem, listItemEventsProcessor: NotificationsViewModelListEventsProcessor) {
        viewDescription.messageText.adjustSpannableClicks()

        setUserAvatar(listItem)
        setUnreadIndicatorVisibility(listItem)
        setCreateTime(listItem)
        setNotificationTypeLabel(viewDescription.notificationTypeIcon, listItem)

        setUserAvatarEvents(listItem, listItemEventsProcessor)
    }

    fun release() {
        viewDescription.userIcon.clear()
        viewDescription.contentIcon.clear()
        viewDescription.notificationTypeIcon.clear()
    }

    protected open fun setUserAvatar(listItem: TItem){
        if(listItem.userId == GlobalConstants.C_BOUNTY_USER_ID) {
            viewDescription.userIcon.setImageResource(R.drawable.ic_golden_coins)
        } else {
            viewDescription.userIcon.loadAvatar(listItem.userAvatar)
        }
    }

    protected open fun setUserAvatarEvents(listItem: TItem, listItemEventsProcessor: NotificationsViewModelListEventsProcessor){
        viewDescription.userIcon.setOnClickListener {
            val userId = listItem.userId
            listItemEventsProcessor.onUserClickedById(UserIdDomain(userId))
        }

        viewDescription.notificationTypeIcon.setOnClickListener {
            val userId = listItem.userId
            listItemEventsProcessor.onUserClickedById(UserIdDomain(userId))
        }
    }

    protected open fun setNotificationTypeLabel(widget: ImageView, listItem: TItem) {
        if(notificationTypeLabelResId == -1){
            widget.visibility = View.INVISIBLE
        } else{
            widget.visibility = View.VISIBLE
            widget.setImageResource(notificationTypeLabelResId)
        }
    }

    protected fun createColorSpan(context: Context, @ColorRes color: Int) =
        ForegroundColorSpan(ContextCompat.getColor(context, color))

    private fun setCreateTime(listItem: BaseNotificationItem){
        val estimationTime = TimeEstimationFormatter.format(listItem.createTime, viewDescription.creationTimeText.context)
        viewDescription.creationTimeText.text = estimationTime
    }

    private fun setUnreadIndicatorVisibility(listItem: BaseNotificationItem){
        if(listItem.isNew){
            viewDescription.unreadIndicatorIcon.visibility = View.VISIBLE
        } else{
            viewDescription.unreadIndicatorIcon.visibility = View.INVISIBLE
        }
    }
}