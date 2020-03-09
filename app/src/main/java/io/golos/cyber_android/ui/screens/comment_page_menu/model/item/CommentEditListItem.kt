package io.golos.cyber_android.ui.screens.comment_page_menu.model.item

import io.golos.cyber_android.ui.shared.recycler_view.versioned.VersionedListItem
import io.golos.utils.id.IdUtil

data class CommentEditListItem(
    override val version: Long = 0,
    override val id: Long = IdUtil.generateLongId(),
    override val isFirstItem: Boolean = false,
    override val isLastItem: Boolean = false
) : VersionedListItem