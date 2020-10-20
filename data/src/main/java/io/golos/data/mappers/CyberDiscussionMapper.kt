package io.golos.data.mappers

import io.golos.commun4j.model.CyberDiscussion
import io.golos.commun4j.model.CyberDiscussionRaw
import io.golos.domain.dto.PostDomain
import io.golos.domain.dto.ReportedPostDomain
import io.golos.domain.posts_parsing_rendering.Attribute
import io.golos.domain.posts_parsing_rendering.mappers.json_to_dto.JsonToDtoMapper
import io.golos.utils.helpers.toAbsoluteUrl
import org.json.JSONObject

fun CyberDiscussionRaw.mapToReportedPostDomain(user: String): ReportedPostDomain {
    val postTitle = try {
        JSONObject(JSONObject(this.document).getString("attributes")).getString(Attribute.TITLE.name.toLowerCase())
    }catch (e:Exception){
        null
    }
    return ReportedPostDomain(
        post = PostDomain(
            title = postTitle?:"",
            author = author.mapToAuthorDomain(),
            community = community.mapToCommunityDomain(),
            contentId = contentId.mapToContentIdDomain(),
            meta = meta.mapToMetaDomain(),
            stats = stats?.mapToStatsDomain(),
            type = type,
            shareUrl = url.toAbsoluteUrl(),
            votes = votes.mapToVotesDomain(),
            isMyPost = user == this.author.userId.name,
            reward = null,
            donation = null,
            viewCount = viewsCount?:0,
            body = this.document?.let { JsonToDtoMapper().map(it) }
        ),
        entityReport =  null,
        reports = reports
    )
}