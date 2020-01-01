package io.golos.cyber_android.ui.screens.post_view.dto.post_list_items

import io.golos.cyber_android.ui.shared.recycler_view.versioned.VersionedListItem

/**
 * Title of a post
 */
data class PostTitleListItem(
    override val id: Long,
    override val version: Long,
    val title: String
) : VersionedListItem