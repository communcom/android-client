package io.golos.cyber_android.ui.mappers

import io.golos.cyber_android.ui.dto.Stats
import io.golos.domain.dto.StatsDomain

fun StatsDomain.mapToStats() =
    Stats(
        commentsCount = this.commentsCount,
        viewCount = this.viewCount
    )