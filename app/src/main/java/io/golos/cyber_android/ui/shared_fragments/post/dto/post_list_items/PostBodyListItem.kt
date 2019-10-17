package io.golos.cyber_android.ui.shared_fragments.post.dto.post_list_items

import io.golos.cyber_android.ui.common.recycler_view.versioned.VersionedListItem
import io.golos.domain.post.post_dto.PostBlock

data class PostBodyListItem(
    override val id: Long,
    override val version: Long,
    val post: PostBlock
) : VersionedListItem