package io.golos.cyber_android.ui.screens.post_view.dto.post_list_items

import io.golos.cyber_android.ui.shared.recycler_view.GroupListItem
import io.golos.cyber_android.ui.shared.recycler_view.versioned.VersionedListItem

/**
 * Title of a post
 */
data class PostTitleListItem(
    override val id: Long,
    override val version: Long,
    override val isFirstItem: Boolean,
    override val isLastItem: Boolean,

    val title: String,
    override val groupId: Int = 0
) : GroupListItem, VersionedListItem