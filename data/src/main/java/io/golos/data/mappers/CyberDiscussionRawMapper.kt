package io.golos.data.mappers

import io.golos.commun4j.model.CyberDiscussionRaw
import io.golos.domain.dto.PostDomain
import io.golos.domain.dto.RewardPostDomain
import io.golos.domain.posts_parsing_rendering.mappers.json_to_dto.JsonToDtoMapper
import io.golos.utils.helpers.toAbsoluteUrl

fun CyberDiscussionRaw.mapToPostDomain(isMyPost: Boolean, reward: RewardPostDomain?): PostDomain {
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
        isMyPost,
        reward = reward
    )
}

fun CyberDiscussionRaw.mapToPostDomain(user: String, reward: RewardPostDomain?): PostDomain {
    return mapToPostDomain(user == this.author.userId.name, reward)
}