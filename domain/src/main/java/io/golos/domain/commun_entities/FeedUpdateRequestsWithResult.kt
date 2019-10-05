package io.golos.domain.rules

import io.golos.commun4j.model.DiscussionsResult
import io.golos.domain.requestmodel.FeedUpdateRequest

data class FeedUpdateRequestsWithResult<Q : FeedUpdateRequest>(
    val feedRequest: Q,
    val discussionsResult: DiscussionsResult
)