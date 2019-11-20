package io.golos.data.mappers

import io.golos.commun4j.model.CyberDiscussionRaw
import io.golos.domain.dto.PostDomain
import io.golos.domain.posts_parsing_rendering.mappers.json_to_dto.JsonToDtoMapper

fun CyberDiscussionRaw.mapToPostDomain(): PostDomain{
    return PostDomain(
        this.author.mapToAuthorDomain(),
        this.community.mapToCommunityDomainMapper(),
        this.contentId.mapToContentIdDomain(),
        JsonToDtoMapper().map(this.document!!),
        this.meta.mapToMetaDomain(),
        null,
        null,
        this.url,
        this.votes.mapToVotesDomain()
    )
}