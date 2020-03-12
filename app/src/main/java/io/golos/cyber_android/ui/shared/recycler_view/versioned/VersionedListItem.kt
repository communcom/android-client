package io.golos.cyber_android.ui.shared.recycler_view.versioned

import io.golos.cyber_android.ui.shared.recycler_view.ListItem

/**
 * To make Diff calculations much move simple
 */
interface VersionedListItem : ListItem {
    val version: Long

    val isFirstItem: Boolean
    val isLastItem: Boolean
}