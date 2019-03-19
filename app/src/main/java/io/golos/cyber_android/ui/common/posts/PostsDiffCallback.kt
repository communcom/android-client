package io.golos.cyber_android.ui.common.posts

import androidx.recyclerview.widget.DiffUtil
import io.golos.domain.entities.PostEntity
import io.golos.domain.interactors.model.PostModel

/**
 * [DiffUtil.ItemCallback] impl for [PostEntity]. Items compares by [PostEntity.contentId] fields,
 * contents compares by objects itself
 */
class PostsDiffCallback: DiffUtil.ItemCallback<PostModel>() {

    override fun areItemsTheSame(oldItem: PostModel, newItem: PostModel) = oldItem.contentId == newItem.contentId

    override fun areContentsTheSame(oldItem: PostModel, newItem: PostModel) = oldItem == newItem
}