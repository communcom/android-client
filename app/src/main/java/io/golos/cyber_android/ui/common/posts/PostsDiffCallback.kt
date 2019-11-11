package io.golos.cyber_android.ui.common.posts

import androidx.recyclerview.widget.DiffUtil
import io.golos.domain.use_cases.model.PostModel

/**
 * [DiffUtil.Callback] impl for [PostModel] lists
 */
class PostsDiffCallback(private val oldList: List<PostModel>, private val newList: List<PostModel>) :
    DiffUtil.Callback() {

    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].contentId == newList[newItemPosition].contentId
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}
