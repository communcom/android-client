package io.golos.domain.rules

import io.golos.cyber4j.model.CyberDiscussion
import io.golos.cyber4j.model.DiscussionsResult
import io.golos.domain.entities.*
import io.golos.domain.model.FeedUpdateRequest

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-03-13.
 */
data class FeedUpdateRequestsWithResult<Q : FeedUpdateRequest>(
    val reedRequest: Q,
    val discussionsResult: DiscussionsResult
)

class CyberPostToEntityMapper : CyberToEntityMapper<CyberDiscussion, PostEntity> {

    override suspend fun invoke(cyberObject: CyberDiscussion): PostEntity {
        return PostEntity(
            DiscussionIdEntity(
                cyberObject.contentId.userId,
                cyberObject.contentId.permlink,
                cyberObject.contentId.refBlockNum
            ),
            DiscussionAuthorEntity(
                CyberUser(cyberObject.author?.userId?.name ?: "unknown"),
                cyberObject.author?.username ?: "unknown"
            ),
            CommunityEntity(cyberObject.community.id, cyberObject.community.name, cyberObject.community.getAvatarUrl),
            DiscussionContent(
                cyberObject.content.title,
                ContentBody(cyberObject.content.body.preview, cyberObject.content.body.full),
                cyberObject.content.metadata ?: ""
            ),
            DiscussionVotes(
                cyberObject.votes.hasUpVote,
                cyberObject.votes.hasDownVote,
                1,1
            ),
            DiscussionCommentsCount(cyberObject.stats.commentsCount),
            DiscussionPayout(cyberObject.payout.rShares),
            DiscussionMetadata(cyberObject.meta.time)
        )
    }
}

class CyberFeedToEntityMapper(val postMapper: CyberToEntityMapper<CyberDiscussion, PostEntity>) :
    CyberToEntityMapper<FeedUpdateRequestsWithResult<FeedUpdateRequest>, FeedEntity<PostEntity>> {

    override suspend fun invoke(cyberObject: FeedUpdateRequestsWithResult<FeedUpdateRequest>): FeedEntity<PostEntity> {
        return FeedEntity(
            cyberObject.discussionsResult.items
                .map { postMapper(it) },
            cyberObject.reedRequest.pageKey,
            cyberObject.discussionsResult.sequenceKey
        )
    }
}