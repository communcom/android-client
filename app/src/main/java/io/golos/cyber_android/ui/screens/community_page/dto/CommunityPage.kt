package io.golos.cyber_android.ui.screens.community_page.dto

import io.golos.domain.dto.CommunityIdDomain
import io.golos.domain.dto.CommunityRuleDomain
import java.util.*

data class CommunityPage(
    val communityId: CommunityIdDomain,
    val name: String,
    val avatarUrl: String?,
    val coverUrl: String?,
    val description: String?,
    val rules: List<CommunityRuleDomain>,
    var isSubscribed: Boolean,
    val isBlocked: Boolean,
    val friendsCount: Int,
    val friends: List<CommunityFriend>,
    val membersCount: Int,
    val leadsCount: Int,
    val communityCurrency: CommunityPageCurrency,
    val joinDate: Date,
    val alias: String?
) {
    data class CommunityPageCurrency(val currencyName: String, val exchangeRate: Float)
}