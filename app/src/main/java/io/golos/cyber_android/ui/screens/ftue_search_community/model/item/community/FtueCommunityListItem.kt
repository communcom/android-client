package io.golos.cyber_android.ui.screens.ftue_search_community.model.item.community

import io.golos.cyber_android.ui.common.recycler_view.versioned.VersionedListItem
import io.golos.cyber_android.ui.dto.Community
import io.golos.domain.utils.IdUtil

data class FtueCommunityListItem(
    val community: Community,
    override val version: Long = 0,
    override val id: Long = IdUtil.generateLongId()
) : VersionedListItem