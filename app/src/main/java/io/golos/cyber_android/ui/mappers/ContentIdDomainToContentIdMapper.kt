package io.golos.cyber_android.ui.mappers

import io.golos.commun4j.model.DiscussionId
import io.golos.domain.dto.PostDomain

class ContentIdDomainToContentIdMapper : Function1<DiscussionId, PostDomain.ContentIdDomain> {

    override fun invoke(discussionId: DiscussionId): PostDomain.ContentIdDomain {
        return PostDomain.ContentIdDomain(
            discussionId.communityId,
            discussionId.permlink,
            discussionId.userId.name
        )
    }
}