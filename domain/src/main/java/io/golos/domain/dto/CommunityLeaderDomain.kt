package io.golos.domain.dto

import io.golos.commun4j.sharedmodel.CyberName

data class CommunityLeaderDomain(
    val userId: CyberName,
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