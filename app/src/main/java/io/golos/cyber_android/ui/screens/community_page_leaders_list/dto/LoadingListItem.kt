package io.golos.cyber_android.ui.screens.community_page_leaders_list.dto

import io.golos.cyber_android.ui.common.recycler_view.versioned.VersionedListItem

data class LoadingListItem(
    override val id: Long,
    override val version: Long
): VersionedListItem