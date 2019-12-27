package io.golos.cyber_android.ui.screens.community_page_leaders_list.dto

import io.golos.commun4j.sharedmodel.CyberName
import io.golos.cyber_android.ui.common.recycler_view.versioned.VersionedListItem

data class LeaderListIem(
    override val id: Long,
    override val version: Long,

    val userId: CyberName,
    val avatarUrl: String?,
    val username: String,
    val rating: Double,
    val ratingPercent: Double
): VersionedListItem