package io.golos.cyber_android.ui.mappers

import io.golos.cyber_android.ui.dto.Post
import io.golos.domain.dto.PostDomain

class ContentIdDomainToContentIdMapper : Function1<PostDomain.ContentIdDomain, Post.ContentId> {

    override fun invoke(contentIdDomain: PostDomain.ContentIdDomain): Post.ContentId {
        return Post.ContentId(
            contentIdDomain.communityId,
            contentIdDomain.permlink,
            contentIdDomain.userId
        )
    }
}