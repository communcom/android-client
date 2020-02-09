package io.golos.cyber_android.ui.screens.profile_photos.dto

import io.golos.cyber_android.ui.shared.recycler_view.versioned.VersionedListItem

data class PhotoGridItem(
    override val id: Long,
    override val version: Long,
    override val isFirstItem: Boolean,
    override val isLastItem: Boolean,

    val imageUri: String,
    val isImageFromCamera: Boolean
) : VersionedListItem