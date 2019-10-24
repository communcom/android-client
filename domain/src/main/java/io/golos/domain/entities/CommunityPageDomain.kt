package io.golos.domain.entities

import java.util.*

data class CommunityPageDomain(
    val communityId: String,
    val name: String,
    val avatarUrl: String?,
    val coverUrl: String?,
    val description: String?,
    val rules: String?,
    val isSubscribed: Boolean,
    val isBlocked: Boolean,
    val friendsCount: Long,
    val friends: List<CommunityFriendDomain>,
    val membersCount: Long,
    val leadsCount: Long,
    val communityCurrency: CommunityPageCurrencyDomain,
    val joinDate: Date
) {

    data class CommunityFriendDomain(
        val userId: String,
        val userName: String,
        val avatarUrl: String,
        val hasAward: Boolean
    )

    data class CommunityPageCurrencyDomain(val currencyName: String, val exchangeRate: Float)
}