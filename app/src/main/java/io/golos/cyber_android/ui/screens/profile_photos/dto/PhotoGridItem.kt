package io.golos.cyber_android.ui.screens.profile_photos.dto

import io.golos.cyber_android.ui.common.recycler_view.versioned.VersionedListItem

data class PhotoGridItem(
    override val id: Long,
    override val version: Long,
    val imageUri: String,
    val isImageFromCamera: Boolean
) : VersionedListItem