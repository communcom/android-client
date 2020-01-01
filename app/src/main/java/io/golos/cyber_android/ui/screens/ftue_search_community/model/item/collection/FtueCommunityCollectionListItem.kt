package io.golos.cyber_android.ui.screens.ftue_search_community.model.item.collection

import io.golos.cyber_android.ui.shared.recycler_view.versioned.VersionedListItem
import io.golos.domain.utils.IdUtil

data class FtueCommunityCollectionListItem(
    val collection: CommunityCollection,
    override val id: Long = IdUtil.generateLongId(),
    override val version: Long = 0

) : VersionedListItem