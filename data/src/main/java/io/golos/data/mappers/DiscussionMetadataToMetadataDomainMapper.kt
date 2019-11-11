package io.golos.data.mappers

import io.golos.commun4j.model.DiscussionMetadata
import io.golos.domain.dto.PostDomain

class DiscussionMetadataToMetadataDomainMapper : Function1<DiscussionMetadata, PostDomain.MetaDomain> {

    override fun invoke(discussionMetadata: DiscussionMetadata): PostDomain.MetaDomain {
        return PostDomain.MetaDomain(discussionMetadata.creationTime)
    }

}