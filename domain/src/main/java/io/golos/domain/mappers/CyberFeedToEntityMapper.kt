package io.golos.domain.mappers

import io.golos.domain.dto.FeedEntity
import io.golos.domain.dto.PostEntity
import io.golos.domain.requestmodel.FeedUpdateRequest
import io.golos.domain.rules.FeedUpdateRequestsWithResult
import javax.inject.Inject

interface CyberFeedToEntityMapper : CommunToEntityMapper<FeedUpdateRequestsWithResult<FeedUpdateRequest>, FeedEntity<PostEntity>>

class CyberFeedToEntityMapperImpl
@Inject
constructor(val postMapper: CyberPostToEntityMapper) : CyberFeedToEntityMapper {

    override fun map(communObject: FeedUpdateRequestsWithResult<FeedUpdateRequest>): FeedEntity<PostEntity> {
        throw UnsupportedOperationException("")
/*
        return FeedEntity(
            communObject.discussionsResult.items
                .map { postMapper.map(it) },
            communObject.feedRequest.pageKey,
            "" //communObject.discussionsResult.sequenceKey ?: feedEndMark  - for DiscussionResult. For now - no idea
        )
*/
    }

    companion object {
        val feedEndMark = "#feed_end_mark#"
    }
}