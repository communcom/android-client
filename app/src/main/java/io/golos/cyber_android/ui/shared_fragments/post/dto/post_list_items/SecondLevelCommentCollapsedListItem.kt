package io.golos.cyber_android.ui.shared_fragments.post.dto.post_list_items

import io.golos.cyber_android.ui.common.recycler_view.versioned.VersionedListItem

data class SecondLevelCommentCollapsedListItem(
    override val id: Long,
    override val version: Long
) : VersionedListItem