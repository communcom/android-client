package io.golos.cyber_android.ui.dialogs.post.model

import io.golos.cyber_android.ui.common.recycler_view.versioned.VersionedListItem
import io.golos.domain.utils.IdUtil

data class RemoveFavoriteListItem(
    override val version: Long = 0,
    override val id: Long = IdUtil.generateLongId()
) : VersionedListItem