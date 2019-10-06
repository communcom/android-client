package io.golos.domain.mappers

import io.golos.domain.entities.FeedEntity
import io.golos.domain.entities.PostEntity
import io.golos.domain.requestmodel.FeedUpdateRequest
import io.golos.domain.rules.FeedUpdateRequestsWithResult
import javax.inject.Inject

interface CyberFeedToEntityMapper : CommunToEntityMapper<FeedUpdateRequestsWithResult<FeedUpdateRequest>, FeedEntity<PostEntity>>

class CyberFeedToEntityMapperImpl
@Inject
constructor(val postMapper: CyberPostToEntityMapper) : CyberFeedToEntityMapper {

    override suspend fun map(communObject: FeedUpdateRequestsWithResult<FeedUpdateRequest>): FeedEntity<PostEntity> {
        return FeedEntity(
            communObject.discussionsResult.items
                .map { postMapper.map(it) },
            communObject.feedRequest.pageKey,
            communObject.discussionsResult.sequenceKey ?: feedEndMark
        )
    }

    companion object {
        val feedEndMark = "#feed_end_mark#"
    }
}