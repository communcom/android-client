package io.golos.cyber_android.ui.screens.followers

import androidx.recyclerview.widget.DiffUtil

class FollowersDiffUtil constructor(
    private val oldCommunitiesList: List<Follower>,
    private val newCommunitiesList: List<Follower>
) : DiffUtil.Callback() {

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldCommunitiesList[oldItemPosition] == newCommunitiesList[newItemPosition]
    }

    override fun getOldListSize(): Int = oldCommunitiesList.size

    override fun getNewListSize(): Int = newCommunitiesList.size

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldCommunitiesList[oldItemPosition] == newCommunitiesList[newItemPosition]
    }
}