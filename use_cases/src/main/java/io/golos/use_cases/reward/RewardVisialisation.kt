package io.golos.use_cases.reward

import io.golos.domain.dto.RewardPostDomain

fun RewardPostDomain?.isRewarded(): Boolean =
    this?.let {
        if(it.isClosed && it.rewardValue.value > 0.0) {
            return@let true
        }

        if(!it.isClosed && it.topCount > 1) {
            return@let true
        }

        false
    } ?: false

fun RewardPostDomain?.getRewardValue(): Double? =
    this?.let {
        if(it.isClosed && it.rewardValue.value > 0.0) {
            return@let it.rewardValue.value
        }

        if(!it.isClosed && it.topCount > 1) {
            return@let null
        }

        null
    }

fun RewardPostDomain?.isTopReward(): Boolean? {
    if(!this.isRewarded()) {
        return null
    }

    return this.getRewardValue() == null
}
