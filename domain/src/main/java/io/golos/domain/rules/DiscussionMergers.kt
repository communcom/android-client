package io.golos.domain.rules

import io.golos.domain.entities.*

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-03-13.
 */
class PostMerger : EntityMerger<PostEntity> {

    override fun invoke(new: PostEntity, old: PostEntity): PostEntity {
        return PostEntity(
            new.contentId, new.author, new.community,
            PostContent(
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
        if (new.pageId == null) return new

        val firstOfNew = new.discussions.first()

        if (old.discussions.any { it.contentId == firstOfNew.contentId }) {
            val lastPos = old.discussions.indexOfLast { it.contentId == firstOfNew.contentId }
            return FeedEntity(old.discussions.subList(0, lastPos) + new.discussions, old.nextPageId, new.nextPageId)
        } else if (old.nextPageId == new.pageId) {
            return FeedEntity(old.discussions + new.discussions, old.nextPageId, new.nextPageId)
        }
        return old
    }
}

class CommentMerger : EntityMerger<CommentEntity> {

    override fun invoke(new: CommentEntity, old: CommentEntity): CommentEntity {
        return CommentEntity(
            new.contentId, new.author,
            CommentContent(
                ContentBody(
                    new.content.body.preview ?: old.content.body.preview,
                    new.content.body.full ?: old.content.body.full
                ),
                new.content.metadata
            ),
            new.votes, new.payout,
            new.parentPostId, new.parentCommentId, new.meta
        )
    }
}

class CommentFeedMerger : EntityMerger<FeedEntity<CommentEntity>> {
    override fun invoke(new: FeedEntity<CommentEntity>, old: FeedEntity<CommentEntity>): FeedEntity<CommentEntity> {
        if (new.discussions.isEmpty()) return old
        if (old.discussions.isEmpty()) return new
        if (new.pageId == null) return new

        val firstOfNew = new.discussions.first()

        if (old.discussions.any { it.contentId == firstOfNew.contentId }) {
            val lastPos = old.discussions.indexOfLast { it.contentId == firstOfNew.contentId }
            return FeedEntity(old.discussions.subList(0, lastPos) + new.discussions, old.nextPageId, new.nextPageId)
        } else if (old.nextPageId == new.pageId) {
            return FeedEntity(old.discussions + new.discussions, old.nextPageId, new.nextPageId)
        }
        return old
    }
}