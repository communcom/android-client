package io.golos.cyber_android.ui.screens.community_page_leaders_list.dto

import io.golos.cyber_android.ui.shared.recycler_view.versioned.VersionedListItem

data class EmptyListItem(
    override val id: Long,
    override val version: Long,
    override val isFirstItem: Boolean = false,
    override val isLastItem: Boolean = false
): VersionedListItem