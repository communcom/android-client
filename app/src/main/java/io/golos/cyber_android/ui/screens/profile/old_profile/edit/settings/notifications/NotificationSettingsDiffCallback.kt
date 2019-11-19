package io.golos.cyber_android.ui.screens.profile.old_profile.edit.settings.notifications

import androidx.recyclerview.widget.DiffUtil

/**
 * [DiffUtil.Callback] impl for [NotificationSetting] lists
 */
class NotificationSettingsDiffCallback(private val oldList: List<NotificationSetting>, private val newList: List<NotificationSetting>) :
    DiffUtil.Callback() {

    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].icon == newList[newItemPosition].icon
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}
