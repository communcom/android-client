package io.golos.data.mappers

import io.golos.commun4j.services.model.GetStateBulkResponseItem
import io.golos.domain.dto.ContentIdDomain
import io.golos.domain.dto.RewardPostDomain
import io.golos.utils.dates.fromServerFormat

fun GetStateBulkResponseItem.mapToRewardPostDomain(): RewardPostDomain {
    return RewardPostDomain(
        topCount = this.topCount,
        collectionEnd = this.collectionEnd.fromServerFormat(),
        rewardValue = this.reward.mapToRewardValueDomain(),
        isClosed = this.isClosed,
        contentId = ContentIdDomain("", this.contentId.permlink, this.contentId.userId.name)
    )
}