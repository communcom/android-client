package io.golos.cyber_android.ui.screens.main_activity.communities.dto

import io.golos.cyber_android.ui.common.recycler_view.versioned.VersionedListItem

/**
 * List item for loading indicator
 */
data class LoadingListItem(
    override val id: Long,
    override val version: Long
): VersionedListItem