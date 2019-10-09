package io.golos.cyber_android.ui.screens.main_activity.communities.tabs.common.dto

import io.golos.cyber_android.ui.common.recycler_view.ListItem
import io.golos.domain.commun_entities.Community

data class CommunityListItem(
    override val id: Long,
    val community: Community,
    val isJoined: Boolean
) : ListItem