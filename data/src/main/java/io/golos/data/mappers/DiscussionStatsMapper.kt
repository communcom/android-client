package io.golos.data.mappers

import io.golos.commun4j.model.DiscussionStats
import io.golos.domain.dto.StatsDomain

fun DiscussionStats.mapToStatsDomain() =
    StatsDomain(
        commentsCount = this.commentsCount ?: 0,
        viewCount = 0
    )
