package io.golos.data.mappers

import io.golos.commun4j.model.CyberDiscussionRaw
import io.golos.domain.dto.PostDomain
import io.golos.domain.posts_parsing_rendering.mappers.json_to_dto.JsonToDtoMapper


class CyberDiscussionRawMapper : Function1<CyberDiscussionRaw, PostDomain> {

    override fun invoke(discussionRaw: CyberDiscussionRaw): PostDomain {
        return PostDomain(
            discussionRaw.author.mapToAuthorDomain(),
            discussionRaw.community.mapToCommunityDomainMapper(),
            discussionRaw.contentId.mapToContentIdDomain(),
            JsonToDtoMapper().map(discussionRaw.document!!),
            discussionRaw.meta.mapToMetaDomain(),
            null,
            null,
            discussionRaw.votes.mapToVotesDomain()
        )
    }
}