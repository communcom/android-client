package io.golos.domain.mappers

import io.golos.domain.entities.CommentEntity
import io.golos.domain.entities.FeedEntity
import io.golos.domain.requestmodel.FeedUpdateRequest
import io.golos.domain.rules.FeedUpdateRequestsWithResult
import javax.inject.Inject

interface CyberCommentsToEntityMapper : CommunToEntityMapper<FeedUpdateRequestsWithResult<FeedUpdateRequest>, FeedEntity<CommentEntity>>

class CyberCommentsToEntityMapperImpl
@Inject
constructor(val postMapper: CyberCommentToEntityMapper) : CyberCommentsToEntityMapper {

    override fun map(communObject: FeedUpdateRequestsWithResult<FeedUpdateRequest>): FeedEntity<CommentEntity> {
        return FeedEntity(
            communObject.discussionsResult.items
                .map { postMapper.map(it) },
            communObject.feedRequest.pageKey,
            ""  // [AS] communObject.discussionsResult.sequenceKey ?: feedEndMark - for DiscussionResult. For now - no idea
        )
    }

    companion object {
        val feedEndMark = "#feed_end_mark#"
    }
}