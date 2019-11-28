package io.golos.domain.dto

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
    val friendsCount: Int,
    val friends: List<CommunityFriendDomain>,
    val membersCount: Int,
    val leadsCount: Int,
    val communityCurrency: CommunityPageCurrencyDomain,
    val joinDate: Date
) {

    data class CommunityFriendDomain(
        val userId: String,
        val userName: String,
        val avatarUrl: String,
        val isLead: Boolean
    )

    data class CommunityPageCurrencyDomain(val currencyName: String, val exchangeRate: Float)
}