package io.golos.cyber_android.ui.screens.post_view.dto.post_list_items

import io.golos.cyber_android.ui.shared.recycler_view.GroupListItem
import io.golos.cyber_android.ui.shared.recycler_view.versioned.VersionedListItem
import io.golos.utils.id.IdUtil

data class EmptyCommentsListItem(
    override val id: Long = IdUtil.generateLongId(),
    override val version: Long = 0,
    override val isFirstItem: Boolean = false,
    override val isLastItem: Boolean = false,
    override val groupId: Int = 4
) : GroupListItem, VersionedListItem