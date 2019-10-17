package io.golos.cyber_android.ui.shared_fragments.post.view.list.list_items

import io.golos.cyber_android.ui.common.recycler_view.versioned.VersionedListItem

/**
 * Title of a post
 */
data class PostTitleListItem(
    override val id: Long,
    override val version: Long,
    val title: String
) : VersionedListItem