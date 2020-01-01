package io.golos.cyber_android.ui.shared.recycler_view.versioned

/**
 * List item for loading indicator
 */
data class RetryListItem(
    override val id: Long,
    override val version: Long
): VersionedListItem