package io.golos.cyber_android.ui.common.recycler_view.versioned

import io.golos.cyber_android.ui.common.recycler_view.versioned.VersionedListItem

/**
 * List item for loading indicator
 */
data class RetryListItem(
    override val id: Long,
    override val version: Long
): VersionedListItem