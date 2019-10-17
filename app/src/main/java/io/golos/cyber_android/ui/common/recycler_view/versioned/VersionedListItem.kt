package io.golos.cyber_android.ui.common.recycler_view.versioned

import io.golos.cyber_android.ui.common.recycler_view.ListItem

/**
 * To make Diff calculations much move simple
 */
interface VersionedListItem : ListItem {
    val version: Long
}