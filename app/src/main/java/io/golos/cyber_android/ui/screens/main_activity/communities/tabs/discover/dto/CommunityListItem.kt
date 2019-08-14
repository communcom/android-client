package io.golos.cyber_android.ui.screens.main_activity.communities.tabs.discover.dto

import io.golos.cyber_android.ui.common.recycler_view.ListItem

data class CommunityListItem(
    override val id: Long,
    val externalId: String,
    val name: String,
    val followersQuantity: Int,
    val logoUrl: String,
    val isJoined: Boolean
) : ListItem