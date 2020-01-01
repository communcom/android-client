package io.golos.cyber_android.ui.screens.post_view.dto.post_list_items

import io.golos.cyber_android.ui.shared.recycler_view.versioned.VersionedListItem

data class FirstLevelCommentLoadingListItem(
    override val id: Long,
    override val version: Long
) : VersionedListItem