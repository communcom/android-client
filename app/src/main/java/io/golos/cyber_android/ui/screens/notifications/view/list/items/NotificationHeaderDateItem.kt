package io.golos.cyber_android.ui.screens.notifications.view.list.items

import io.golos.cyber_android.ui.shared.recycler_view.versioned.VersionedListItem

data class NotificationHeaderDateItem(override val version: Long, override val id: Long, val dateLabel: String): VersionedListItem