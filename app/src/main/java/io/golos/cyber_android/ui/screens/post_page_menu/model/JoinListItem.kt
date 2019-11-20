package io.golos.cyber_android.ui.screens.post_page_menu.model

import io.golos.cyber_android.ui.common.recycler_view.versioned.VersionedListItem
import io.golos.domain.utils.IdUtil

data class JoinListItem(
    val communityId: String,
    override val version: Long = 0,
    override val id: Long = IdUtil.generateLongId()
) : VersionedListItem