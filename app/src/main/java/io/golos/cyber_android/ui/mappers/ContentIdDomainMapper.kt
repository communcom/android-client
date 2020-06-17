package io.golos.cyber_android.ui.mappers

import io.golos.domain.dto.ContentIdDomain

fun ContentIdDomain.mapToContentId(): ContentIdDomain {
    return ContentIdDomain(
        this.communityId,
        this.permlink,
        this.userId
    )
}