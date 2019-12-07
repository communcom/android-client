package io.golos.cyber_android.ui.mappers

import io.golos.cyber_android.ui.dto.Community
import io.golos.cyber_android.ui.screens.ftue_search_community.model.item.FtueCommunityListItem
import io.golos.domain.dto.CommunityDomain

fun List<CommunityDomain>.mapToCommunityList(): List<Community> = map { community -> community.mapToCommunity() }

fun List<Community>.mapToCommunityListItem(): List<FtueCommunityListItem> = map { community ->
    FtueCommunityListItem(community)
}