package io.golos.cyber_android.ui.screens.profile_photos.dto

import io.golos.cyber_android.ui.shared.recycler_view.versioned.VersionedListItem

data class CameraGridItem(
    override val id: Long,
    override val version: Long
) : VersionedListItem