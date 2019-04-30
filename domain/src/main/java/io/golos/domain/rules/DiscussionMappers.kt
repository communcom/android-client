package io.golos.domain.rules

import io.golos.cyber4j.model.CyberDiscussion
import io.golos.cyber4j.model.DiscussionsResult
import io.golos.domain.entities.*
import io.golos.domain.requestmodel.FeedUpdateRequest

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-03-13.
 */
data class FeedUpdateRequestsWithResult<Q : FeedUpdateRequest>(
    val feedRequest: Q,
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
            CommunityEntity(
                cyberObject.community!!.id,
                cyberObject.community!!.name,
                cyberObject.community!!.getAvatarUrl
            ),
            PostContent(
                cyberObject.content.title!!,
                ContentBody(cyberObject.content.body.preview, cyberObject.content.body.full),
                ""
            ),
            DiscussionVotes(
                cyberObject.votes.hasUpVote,
                cyberObject.votes.hasDownVote,
                cyberObject.votes.upCount,
                cyberObject.votes.downCount
            ),
            DiscussionCommentsCount(cyberObject.stats!!.commentsCount),
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
            cyberObject.feedRequest.pageKey,
            cyberObject.discussionsResult.sequenceKey ?: feedEndMark
        )
    }

    companion object {
        val feedEndMark = "#feed_end_mark#"
    }
}

class CyberCommentToEntityMapper : CyberToEntityMapper<CyberDiscussion, CommentEntity> {

    override suspend fun invoke(cyberObject: CyberDiscussion): CommentEntity {
        return CommentEntity(
            DiscussionIdEntity(
                cyberObject.contentId.userId,
                cyberObject.contentId.permlink,
                cyberObject.contentId.refBlockNum
            ),
            DiscussionAuthorEntity(
                CyberUser(cyberObject.author?.userId?.name ?: "unknown"),
                cyberObject.author?.username ?: "unknown"
            ),
            CommentContent(
                ContentBody(cyberObject.content.body.preview, cyberObject.content.body.full),
                 ""
            ),
            DiscussionVotes(
                cyberObject.votes.hasUpVote,
                cyberObject.votes.hasDownVote,
                cyberObject.votes.upCount,
                cyberObject.votes.downCount
            ),
            DiscussionPayout(cyberObject.payout.rShares),
            cyberObject.parent!!.post!!.contentId.let {
                DiscussionIdEntity(it.userId, it.permlink, it.refBlockNum)
            },
            cyberObject.parent?.comment?.contentId?.let {
                DiscussionIdEntity(it.userId, it.permlink, it.refBlockNum)
            },
            DiscussionMetadata(cyberObject.meta.time)
        )
    }
}

class CyberCommentsToEntityMapper(val postMapper: CyberToEntityMapper<CyberDiscussion, CommentEntity>) :
    CyberToEntityMapper<FeedUpdateRequestsWithResult<FeedUpdateRequest>, FeedEntity<CommentEntity>> {

    override suspend fun invoke(cyberObject: FeedUpdateRequestsWithResult<FeedUpdateRequest>): FeedEntity<CommentEntity> {
        return FeedEntity(
            cyberObject.discussionsResult.items
                .map { postMapper(it) },
            cyberObject.feedRequest.pageKey,
            cyberObject.discussionsResult.sequenceKey ?: feedEndMark
        )
    }

    companion object {
        val feedEndMark = "#feed_end_mark#"
    }
}