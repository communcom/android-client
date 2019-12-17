package io.golos.cyber_android.ui.screens.profile_comment_page_menu.model.item

import io.golos.cyber_android.ui.common.recycler_view.versioned.VersionedListItem
import io.golos.domain.utils.IdUtil

data class CommentDeleteListItem(
    override val version: Long = 0,
    override val id: Long = IdUtil.generateLongId()
) : VersionedListItem