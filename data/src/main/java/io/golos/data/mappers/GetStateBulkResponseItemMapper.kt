package io.golos.data.mappers

import io.golos.commun4j.services.model.GetStateBulkResponseItem
import io.golos.domain.dto.CommunityIdDomain
import io.golos.domain.dto.ContentIdDomain
import io.golos.domain.dto.RewardPostDomain
import io.golos.domain.dto.UserIdDomain
import io.golos.utils.format.DatesServerFormatter

fun GetStateBulkResponseItem.mapToRewardPostDomain(): RewardPostDomain {
    return RewardPostDomain(
        topCount = this.topCount,
        collectionEnd = DatesServerFormatter.formatFromServer(this.collectionEnd),
        rewardValue = this.reward.mapToRewardValueDomain(),
        isClosed = this.isClosed,
        contentId = ContentIdDomain(CommunityIdDomain(""), this.contentId.permlink, UserIdDomain(this.contentId.userId.name))
    )
}