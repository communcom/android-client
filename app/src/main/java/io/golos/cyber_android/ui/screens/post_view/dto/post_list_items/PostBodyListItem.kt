package io.golos.cyber_android.ui.screens.post_view.dto.post_list_items

import io.golos.cyber_android.ui.shared.recycler_view.GroupListItem
import io.golos.cyber_android.ui.shared.recycler_view.versioned.VersionedListItem
import io.golos.domain.use_cases.post.post_dto.ContentBlock

data class PostBodyListItem(
    override val id: Long,
    override val version: Long,
    val post: ContentBlock,
    override val groupId: Int = 1
    ) : GroupListItem, VersionedListItem