package io.golos.cyber_android.ui.shared_fragments.post.view.list.list_items

import io.golos.cyber_android.ui.common.recycler_view.versioned.VersionedListItem

data class CommentsTitleListItem(
    override val id: Long,
    override val version: Long
) : VersionedListItem