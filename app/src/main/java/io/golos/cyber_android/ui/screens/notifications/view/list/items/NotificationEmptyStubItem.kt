package io.golos.cyber_android.ui.screens.notifications.view.list.items

import io.golos.cyber_android.ui.shared.recycler_view.versioned.VersionedListItem

data class NotificationEmptyStubItem(
    override val version: Long,
    override val id: Long,
    override val isFirstItem: Boolean = false,
    override val isLastItem: Boolean = false
): VersionedListItem