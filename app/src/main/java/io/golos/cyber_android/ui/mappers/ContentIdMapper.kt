package io.golos.cyber_android.ui.mappers

import io.golos.cyber_android.ui.dto.ContentId
import io.golos.domain.dto.ContentIdDomain

fun ContentId.mapToContentIdDomain(): ContentIdDomain {
    return ContentIdDomain(
        communityId,
        permlink,
        userId
    )
}