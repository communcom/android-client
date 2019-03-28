package io.golos.cyber_android.ui.common.comments

import androidx.recyclerview.widget.DiffUtil
import io.golos.domain.interactors.model.CommentModel
import io.golos.domain.interactors.model.PostModel

/**
 * [DiffUtil.Callback] impl for [PostModel] lists
 */
class CommentsDiffCallback(private val oldList: List<CommentModel>, private val newList: List<CommentModel>) :
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
