package io.golos.data.mappers

import io.golos.commun4j.model.DiscussionId
import io.golos.domain.dto.PostDomain

class DiscussionIdToContentIdDomainMapper : Function1<DiscussionId, PostDomain.ContentIdDomain> {

    override fun invoke(discussionId: DiscussionId): PostDomain.ContentIdDomain {
        return PostDomain.ContentIdDomain(
            discussionId.communityId,
            discussionId.permlink,
            discussionId.userId.name
        )
    }
}