package io.golos.cyber_android.ui.mappers

import io.golos.cyber_android.ui.dto.Community
import io.golos.cyber_android.ui.screens.ftue_search_community.model.item.collection.CommunityCollection
import io.golos.cyber_android.ui.screens.ftue_search_community.model.item.collection.FtueCommunityCollectionListItem
import io.golos.cyber_android.ui.screens.ftue_search_community.model.item.community.FtueCommunityListItem

fun List<Community>.mapToCommunityListItem(): List<FtueCommunityListItem> = map { community ->
    FtueCommunityListItem(community)
}

fun List<Community?>.mapToCollectionList(): List<CommunityCollection> = map { community ->
    CommunityCollection(community)
}

fun List<CommunityCollection>.mapToCollectionListItem(): List<FtueCommunityCollectionListItem> = map { community ->
    FtueCommunityCollectionListItem(community)
}