package io.golos.cyber_android.ui.screens.community_page

import java.util.*

data class CommunityPage(
    val communityId: String,
    val name: String,
    val avatarUrl: String?,
    val coverUrl: String?,
    val description: String?,
    val rules: String?,
    val isSubscribed: Boolean,
    val isBlocked: Boolean,
    val friendsCount: Long,
    val friends: List<CommunityFriend>,
    val membersCount: Long,
    val leadsCount: Long,
    val communityCurrency: CommunityPageCurrency,
    val joinDate: Date
) {

    data class CommunityFriend(
        val userId: String,
        val userName: String,
        val avatarUrl: String,
        val hasAward: Boolean
    )

    data class CommunityPageCurrency(val currencyName: String, val exchangeRate: Float)
}