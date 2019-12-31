package io.golos.cyber_android.ui.screens.feed_my.view.list

import androidx.recyclerview.widget.DiffUtil
import io.golos.cyber_android.ui.dto.Post

class MyFeedDiffUtil constructor(
    private val oldPostsList: List<Post>,
    private val newPostsList: List<Post>
) : DiffUtil.Callback() {

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldPostsList[oldItemPosition] == newPostsList[newItemPosition]
    }

    override fun getOldListSize(): Int = oldPostsList.size

    override fun getNewListSize(): Int = newPostsList.size

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldPostsList[oldItemPosition] == newPostsList[newItemPosition]
    }
}