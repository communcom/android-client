package io.golos.domain.rules

import io.golos.domain.entities.ContentBody
import io.golos.domain.entities.DiscussionContent
import io.golos.domain.entities.FeedEntity
import io.golos.domain.entities.PostEntity

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-03-13.
 */
class PostMerger : EntityMerger<PostEntity> {

    override fun invoke(new: PostEntity, old: PostEntity): PostEntity {
        return PostEntity(
            new.contentId, new.author, new.community,
            DiscussionContent(
                new.content.title,
                ContentBody(
                    new.content.body.preview ?: old.content.body.preview,
                    new.content.body.full ?: old.content.body.full
                ),
                new.content.metadata
            ),
            new.votes, new.comments, new.payout, new.meta
        )
    }
}

class PostFeedMerger : EntityMerger<FeedEntity<PostEntity>> {
    override fun invoke(new: FeedEntity<PostEntity>, old: FeedEntity<PostEntity>): FeedEntity<PostEntity> {
        if (new.discussions.isEmpty()) return new
        if (old.discussions.isEmpty()) return new

        val firstOfNew = new.discussions.first()

        if (old.discussions.any { it.contentId == firstOfNew.contentId }) {
            val lastPos = old.discussions.indexOfLast { it.contentId == firstOfNew.contentId }
            return FeedEntity(old.discussions.subList(0, lastPos) + new.discussions, new.nextPageId)
        }
        return new
    }
}