package io.golos.data.mappers

import io.golos.commun4j.model.CyberDiscussionRaw
import io.golos.domain.dto.DonationsDomain
import io.golos.domain.dto.PostDomain
import io.golos.domain.dto.RewardPostDomain
import io.golos.domain.posts_parsing_rendering.mappers.json_to_dto.JsonToDtoMapper
import io.golos.utils.helpers.toAbsoluteUrl
import io.golos.domain.posts_parsing_rendering.Attribute
import org.json.JSONObject

fun CyberDiscussionRaw.mapToPostDomain(isMyPost: Boolean, reward: RewardPostDomain?, donation: DonationsDomain?): PostDomain {
    val postTitle = try {
        JSONObject(JSONObject(this.document).getString("attributes")).getString(Attribute.TITLE.name.toLowerCase())
    }catch (e:Exception){
        null
    }
    return PostDomain(
        title = postTitle?:"",
        author = this.author.mapToAuthorDomain(),
        community = this.community.mapToCommunityDomain(),
        contentId = this.contentId.mapToContentIdDomain(),
        body = this.document?.let { JsonToDtoMapper().map(it) },
        meta = this.meta.mapToMetaDomain(),
        stats = this.stats?.mapToStatsDomain(),
        type = this.type,
        shareUrl = this.url.toAbsoluteUrl(),
        votes = this.votes.mapToVotesDomain(),
        isMyPost = isMyPost,
        reward = reward,
        donation = donation,
        viewCount = viewsCount ?: 0
    )
}

fun CyberDiscussionRaw.mapToPostDomain(user: String, reward: RewardPostDomain?, donation: DonationsDomain?): PostDomain {
    return mapToPostDomain(user == this.author.userId.name, reward, donation)
}