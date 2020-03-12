package io.golos.data.mappers

import io.golos.commun4j.sharedmodel.CyberAsset
import io.golos.domain.dto.RewardValueDomain

fun CyberAsset.mapToRewardValueDomain(): RewardValueDomain =
    this.amount.split(" ").let {
        RewardValueDomain(
            it[0].toDouble(),
            it[1]
        )
    }