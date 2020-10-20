package io.golos.domain.dto

import java.util.*

/**
 * Reward for a post
 */
data class RewardPostDomain (
    val topCount: Int,
    val collectionEnd: Date,
    val rewardValue: RewardValueDomain,
    val rewardValueCommun: Double?,
    val rewardValueUSD: Double?,
    val isClosed: Boolean,
    val contentId: ContentIdDomain
)