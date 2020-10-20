package io.golos.cyber_android.ui.screens.ftue_search_community.model.item.community

import io.golos.cyber_android.ui.dto.Community
import io.golos.cyber_android.ui.shared.recycler_view.versioned.VersionedListItem
import io.golos.utils.id.IdUtil

data class FtueCommunityListItem(
    var community: Community,

    override val id: Long = IdUtil.generateLongId(),
    override val version: Long = 0,
    override val isFirstItem: Boolean = false,
    override val isLastItem: Boolean = false
) : VersionedListItem