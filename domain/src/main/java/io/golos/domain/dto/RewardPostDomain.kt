package io.golos.domain.dto

import java.util.*

/**
 * Reward for a post
 */
data class RewardPostDomain (
    val topCount: Int,
    val collectionEnd: Date,
    val rewardValue: RewardValueDomain,
    val isClosed: Boolean,
    val contentId: ContentIdDomain
)