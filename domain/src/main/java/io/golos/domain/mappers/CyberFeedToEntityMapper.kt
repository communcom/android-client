package io.golos.domain.mappers

import io.golos.commun4j.model.CyberDiscussion
import io.golos.domain.entities.FeedEntity
import io.golos.domain.entities.PostEntity
import io.golos.domain.requestmodel.FeedUpdateRequest
import io.golos.domain.rules.FeedUpdateRequestsWithResult
import javax.inject.Inject

class CyberFeedToEntityMapper
@Inject
constructor(val postMapper: CommunToEntityMapper<CyberDiscussion, PostEntity>) :
    CommunToEntityMapper<FeedUpdateRequestsWithResult<FeedUpdateRequest>, FeedEntity<PostEntity>> {

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