package io.golos.cyber_android.ui.mappers

import io.golos.cyber_android.ui.dto.ContentId
import io.golos.cyber_android.ui.dto.Post
import io.golos.domain.dto.ContentIdDomain
import io.golos.domain.dto.PostDomain

fun ContentIdDomain.mapToContentId(): ContentId {
    return ContentId(
        this.communityId,
        this.permlink,
        this.userId
    )
}