package io.golos.cyber_android.ui.common.recycler_view.versioned

/**
 * List item for loading indicator
 */
data class LoadingListItem(
    override val id: Long,
    override val version: Long
): VersionedListItem