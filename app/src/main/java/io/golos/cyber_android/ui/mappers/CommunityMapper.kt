package io.golos.cyber_android.ui.mappers

import io.golos.cyber_android.ui.dto.Community
import io.golos.cyber_android.ui.screens.ftue_search_community.model.item.collection.CommunityCollection
import io.golos.cyber_android.ui.screens.ftue_search_community.model.item.collection.FtueCommunityCollectionListItem
import io.golos.cyber_android.ui.screens.ftue_search_community.model.item.community.FtueCommunityListItem
import io.golos.domain.dto.CommunityDomain

fun List<Community>.mapToCommunityListItem(): List<FtueCommunityListItem> = map { community ->
    FtueCommunityListItem(community)
}

fun List<CommunityCollection>.mapToCollectionListItem(): List<FtueCommunityCollectionListItem> = map { community ->
    FtueCommunityCollectionListItem(community)
}

fun CommunityCollection.mapToCollectionListItem(): FtueCommunityCollectionListItem{
    return FtueCommunityCollectionListItem(this)
}


fun CommunityCollection.mapToCollectionListItem(id: Long): FtueCommunityCollectionListItem{
    return FtueCommunityCollectionListItem(this, id)
}

fun List<Community>.mapToCommunityDomainList(): List<CommunityDomain> = map { community ->
    community.mapToCommunityDomain()
}

fun Community.mapToCommunityDomain() =
    CommunityDomain (
        communityId = communityId,
        alias = alias,
        name = name,
        avatarUrl = avatarUrl,
        coverUrl = coverUrl,
        subscribersCount = subscribersCount,
        postsCount = postsCount,
        isSubscribed = isSubscribed)