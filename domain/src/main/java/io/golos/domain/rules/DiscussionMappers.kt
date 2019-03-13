package io.golos.domain.rules

import io.golos.cyber4j.model.CyberDiscussion
import io.golos.cyber4j.model.DiscussionsResult
import io.golos.domain.entities.*

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-03-13.
 */
class PostMapper : CyberToEntityMapper<CyberDiscussion, PostEntity> {

    override suspend fun invoke(cyberObject: CyberDiscussion): PostEntity {
        return PostEntity(
            DiscussionId(
                cyberObject.contentId.userId,
                cyberObject.contentId.permlink,
                cyberObject.contentId.refBlockNum
            ),
            DiscussionAuthorEntity(cyberObject.author.userId.name, cyberObject.author.username),
            CyberCommunity(cyberObject.community.id, cyberObject.community.name, cyberObject.community.getAvatarUrl),
            DiscussionContent(
                cyberObject.content.title,
                ContentBody(cyberObject.content.body.preview, cyberObject.content.body.full),
                cyberObject.content.metadata
            ),
            DiscussionVotes(cyberObject.votes.hasUpVote, cyberObject.votes.hasDownVote),
            DiscussionCommentsCount(cyberObject.comments.count),
            DiscussionPayout(cyberObject.payout.rShares),
            DiscussionMetadata(cyberObject.meta.time)
        )
    }
}

class PostsFeedMapper(val postMapper: CyberToEntityMapper<CyberDiscussion, PostEntity>) :
    CyberToEntityMapper<DiscussionsResult, FeedEntity<PostEntity>> {
    override suspend fun invoke(cyberObject: DiscussionsResult): FeedEntity<PostEntity> {
        return FeedEntity(cyberObject.items.map { postMapper(it) }, cyberObject.sequenceKey)
    }
}