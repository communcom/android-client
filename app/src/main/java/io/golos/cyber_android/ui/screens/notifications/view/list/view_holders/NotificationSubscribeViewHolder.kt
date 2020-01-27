package io.golos.cyber_android.ui.screens.notifications.view.list.view_holders

import android.view.View
import android.view.ViewGroup
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.screens.notifications.view.list.items.NotificationSubscribeItem
import io.golos.cyber_android.ui.screens.notifications.view_model.NotificationsViewModelListEventsProcessor
import io.golos.cyber_android.ui.shared.glide.loadAvatar
import io.golos.cyber_android.ui.shared.recycler_view.ViewHolderBase
import kotlinx.android.synthetic.main.item_notification.view.*

class NotificationSubscribeViewHolder (
    parentView: ViewGroup
) : ViewHolderBase<NotificationsViewModelListEventsProcessor, NotificationSubscribeItem>(
    parentView,
    R.layout.item_notification
) {
    override fun init(listItem: NotificationSubscribeItem, listItemEventsProcessor: NotificationsViewModelListEventsProcessor) {
        itemView.ivUserAvatar.loadAvatar(listItem.userAvatar)
        if(listItem.isNew){
            itemView.ivUnreadIndicator.visibility = View.VISIBLE
        } else{
            itemView.ivUnreadIndicator.visibility = View.INVISIBLE
        }

    }

    fun setupAction(listItem: NotificationSubscribeItem, listItemEventsProcessor: NotificationsViewModelListEventsProcessor){
        itemView.flAction.visibility = View.VISIBLE
        itemView.btnFollow.visibility = View.VISIBLE
        itemView.ivContent.visibility = View.GONE
        itemView.btnFollow.setOnClickListener {

        }
    }
}