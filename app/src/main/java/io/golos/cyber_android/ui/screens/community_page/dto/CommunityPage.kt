package io.golos.cyber_android.ui.screens.community_page.dto

import java.util.*

data class CommunityPage(
    val communityId: String,
    val name: String,
    val avatarUrl: String?,
    val coverUrl: String?,
    val description: String?,
    val rules: String?,
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