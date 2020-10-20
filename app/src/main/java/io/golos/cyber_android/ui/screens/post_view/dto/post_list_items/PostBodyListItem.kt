package io.golos.cyber_android.ui.screens.post_view.dto.post_list_items

import io.golos.cyber_android.ui.shared.recycler_view.GroupListItem
import io.golos.cyber_android.ui.shared.recycler_view.versioned.VersionedListItem
import io.golos.domain.posts_parsing_rendering.post_metadata.post_dto.ContentBlock

data class PostBodyListItem(
    override val id: Long,
    override val version: Long,
    override val isFirstItem: Boolean,
    override val isLastItem: Boolean,
    val post: ContentBlock,
    override val groupId: Int = 1
) : GroupListItem, VersionedListItem