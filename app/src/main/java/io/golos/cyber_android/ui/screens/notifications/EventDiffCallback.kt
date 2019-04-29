package io.golos.cyber_android.ui.screens.notifications

import androidx.recyclerview.widget.DiffUtil
import io.golos.domain.model.EventModel

/**
 * [DiffUtil.Callback] impl for [EventModel] lists
 */
class EventDiffCallback(private val oldList: List<EventModel>, private val newList: List<EventModel>) :
    DiffUtil.Callback() {

    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].eventId.compareTo(newList[newItemPosition].eventId) == 0
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}
