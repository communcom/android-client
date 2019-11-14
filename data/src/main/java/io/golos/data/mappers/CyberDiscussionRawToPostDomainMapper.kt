package io.golos.data.mappers

import io.golos.commun4j.model.CyberDiscussionRaw
import io.golos.domain.dto.PostDomain
import io.golos.domain.posts_parsing_rendering.mappers.json_to_dto.JsonToDtoMapper


class CyberDiscussionRawToPostDomainMapper : Function1<CyberDiscussionRaw, PostDomain> {

    override fun invoke(discussionRaw: CyberDiscussionRaw): PostDomain {
        return PostDomain(
            DiscussionAuthorToAuthorDomainMapper().invoke(discussionRaw.author),
            CyberCommunityToCommunityDomainMapper().invoke(discussionRaw.community),
            DiscussionIdToContentIdDomainMapper().invoke(discussionRaw.contentId),
            JsonToDtoMapper().map(discussionRaw.document!!),
            DiscussionMetadataToMetadataDomainMapper().invoke(discussionRaw.meta),
            null,
            null,
            DiscussionVotesToVotesDomainMapper().invoke(discussionRaw.votes)
        )
    }
}