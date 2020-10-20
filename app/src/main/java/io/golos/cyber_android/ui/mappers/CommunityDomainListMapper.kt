package io.golos.cyber_android.ui.mappers

import io.golos.cyber_android.ui.dto.Community
import io.golos.domain.dto.CommunityDomain

fun List<CommunityDomain>.mapToCommunityList(): List<Community> = map { community -> community.mapToCommunity() }