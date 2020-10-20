package io.golos.cyber_android.ui.screens.subscriptions

import androidx.recyclerview.widget.DiffUtil

class CommunitiesDiffUtil constructor(private val oldCommunitiesList: List<Community>,
                                      private val newCommunitiesList: List<Community>): DiffUtil.Callback() {

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldCommunitiesList[oldItemPosition] == newCommunitiesList[newItemPosition]
    }

    override fun getOldListSize(): Int = oldCommunitiesList.size

    override fun getNewListSize(): Int = newCommunitiesList.size

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldCommunitiesList[oldItemPosition] == newCommunitiesList[newItemPosition]
    }
}