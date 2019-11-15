package io.golos.cyber_android.ui.mappers

import io.golos.cyber_android.ui.dto.Post
import io.golos.domain.dto.PostDomain

fun PostDomain.ContentIdDomain.mapToContentId(): Post.ContentId {
    return Post.ContentId(
        this.communityId,
        this.permlink,
        this.userId
    )
}