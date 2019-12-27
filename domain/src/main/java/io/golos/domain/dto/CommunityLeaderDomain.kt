package io.golos.domain.dto

data class CommunityLeaderDomain(
    val userId: UserIdDomain,
    val avatarUrl: String?,
    val isActive: Boolean,
    val isSubscribed: Boolean,
    val isVoted: Boolean,
    val position: Int,
    val rating: Double,
    val ratingPercent: Double,
    val url: String,
    val username: String,
    val votesCount: Int
)