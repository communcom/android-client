package io.golos.data.mappers

import io.golos.commun4j.model.CyberDiscussionRaw
import io.golos.domain.dto.PostDomain


class CyberDiscussionRawToPostDomainMapper : Function1<CyberDiscussionRaw, PostDomain> {

    override fun invoke(discussionRaw: CyberDiscussionRaw): PostDomain {
        return PostDomain(
            DiscussionAuthorToAuthorDomainMapper().invoke(discussionRaw.author),
            CyberCommunityToCommunityDomainMapper().invoke(discussionRaw.community),
            DiscussionIdToContentIdDomainMapper().invoke(discussionRaw.contentId),
            DocumentRawToDocumentDomainMapper().invoke(discussionRaw.document),
            DiscussionMetadataToMetadataDomainMapper().invoke(discussionRaw.meta),
            null,
            null,
            DiscussionVotesToVotesDomainMapper().invoke(discussionRaw.votes)
        )
    }
}