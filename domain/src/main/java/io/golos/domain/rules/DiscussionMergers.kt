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
                    if (new.content.body.preview.isNotEmpty()) new.content.body.preview else old.content.body.preview,
                    if (new.content.body.full.isNotEmpty()) new.content.body.full else old.content.body.full,
                    new.content.body.embeds,
                    if (new.content.body.mobilePreview.isNotEmpty()) new.content.body.mobilePreview else old.content.body.mobilePreview
                ),
                new.content.tags
            ),
            new.votes, new.comments, new.payout, new.meta, new.stats
        )
    }
}

class PostFeedMerger : EntityMerger<FeedRelatedData<PostEntity>> {
    override fun invoke(
        new: FeedRelatedData<PostEntity>,
        old: FeedRelatedData<PostEntity>
    ): FeedRelatedData<PostEntity> {
        val oldFeed = old.feed
        val newFeed = new.feed

        if (newFeed.discussions.isEmpty()) return new
        if (oldFeed.discussions.isEmpty()) return new
        if (newFeed.pageId == null) return new

        val firstOfNew = newFeed.discussions.first()

        if (oldFeed.discussions.any { it.contentId == firstOfNew.contentId }) {
            val lastPos = oldFeed.discussions.indexOfLast { it.contentId == firstOfNew.contentId }
            return FeedRelatedData(
                FeedEntity(
                    oldFeed.discussions.subList(0, lastPos) + newFeed.discussions,
                    oldFeed.nextPageId,
                    newFeed.nextPageId
                ),
                emptySet()
            )
        } else if (oldFeed.nextPageId == newFeed.pageId) {
            return FeedRelatedData(
                FeedEntity(
                    oldFeed.discussions + newFeed.discussions,
                    oldFeed.nextPageId,
                    newFeed.nextPageId
                ), emptySet()
            )
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
                    if (new.content.body.preview.isNotEmpty()) new.content.body.preview else old.content.body.preview,
                    if (new.content.body.full.isNotEmpty()) new.content.body.full else old.content.body.full,
                    if (new.content.body.embeds.isNotEmpty()) new.content.body.embeds else old.content.body.embeds,
                    if (new.content.body.mobilePreview.isNotEmpty()) new.content.body.mobilePreview else old.content.body.mobilePreview
                )
            ),
            new.votes, new.payout,
            new.parentPostId, new.parentCommentId, new.meta, new.stats
        )
    }
}

class CommentFeedMerger : EntityMerger<FeedRelatedData<CommentEntity>> {
    override fun invoke(
        new: FeedRelatedData<CommentEntity>,
        old: FeedRelatedData<CommentEntity>
    ): FeedRelatedData<CommentEntity> {

        val oldFeed = old.feed
        val newFeed = new.feed

        if (newFeed.discussions.isEmpty()) return old
        if (oldFeed.discussions.isEmpty()) return new
        if (newFeed.pageId == null) return FeedRelatedData(new.feed, emptySet())


        val newDiscussions = newFeed.discussions.filter { !new.fixedPositionEntities.contains(it) }

        return FeedRelatedData(
            FeedEntity(oldFeed.discussions + newDiscussions, oldFeed.nextPageId, newFeed.nextPageId),
            new.fixedPositionEntities
        )
    }
}