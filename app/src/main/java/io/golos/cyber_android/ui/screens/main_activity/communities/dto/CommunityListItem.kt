package io.golos.cyber_android.ui.screens.main_activity.communities.dto

import io.golos.cyber_android.ui.common.recycler_view.ListItem
import io.golos.domain.dto.CommunityDomain

data class CommunityListItem(
    override val id: Long,
    val community: CommunityDomain,
    val isJoined: Boolean
) : ListItem