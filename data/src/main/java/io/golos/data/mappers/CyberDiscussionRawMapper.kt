package io.golos.data.mappers

import io.golos.commun4j.model.CyberDiscussionRaw
import io.golos.domain.dto.PostDomain
import io.golos.domain.posts_parsing_rendering.mappers.json_to_dto.JsonToDtoMapper
import io.golos.utils.toAbsoluteUrl

fun CyberDiscussionRaw.mapToPostDomain(isMyPost: Boolean): PostDomain {
    return PostDomain(
        this.author.mapToAuthorDomain(),
        this.community.mapToCommunityDomain(),
        this.contentId.mapToContentIdDomain(),
        this.document?.let { JsonToDtoMapper().map(it) },
        this.meta.mapToMetaDomain(),
        null,
        null,
        this.url.toAbsoluteUrl(),
        this.votes.mapToVotesDomain(),
        isMyPost
    )
}

fun CyberDiscussionRaw.mapToPostDomain(user: String): PostDomain {
    return mapToPostDomain(user == this.author.userId.name)
}