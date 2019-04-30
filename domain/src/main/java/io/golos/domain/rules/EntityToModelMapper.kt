package io.golos.domain.rules

import androidx.annotation.WorkerThread
import io.golos.domain.Entity
import io.golos.domain.HtmlToSpannableTransformer
import io.golos.domain.Model
import io.golos.domain.asElapsedTime
import io.golos.domain.entities.*
import io.golos.domain.interactors.model.*
import io.golos.domain.requestmodel.QueryResult
import io.golos.domain.requestmodel.VoteRequestModel
import java.util.*
import kotlin.collections.HashMap

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-03-18.
 */
interface EntityToModelMapper<E : Entity, M : Model> {
    @WorkerThread
    suspend operator fun invoke(entity: E): M
}

class PostEntityEntitiesToModelMapper(private val htmlToSpannableTransformer: HtmlToSpannableTransformer) :

    EntityToModelMapper<DiscussionRelatedEntities<PostEntity>, PostModel> {
    private val cashedValues = Collections.synchronizedMap(HashMap<DiscussionRelatedEntities<PostEntity>, PostModel>())
    private val cashedSpans = Collections.synchronizedMap(HashMap<String, CharSequence>())

    override suspend fun invoke(entity: DiscussionRelatedEntities<PostEntity>): PostModel {
        val post = entity.discussionEntity

        val voteEntity = entity.voteStateEntity

        val out = cashedValues.getOrPut(entity) {
            PostModel(
                DiscussionIdModel(post.contentId.userId, post.contentId.permlink, post.contentId.refBlockNum),
                DiscussionAuthorModel(post.author.userId, post.author.username),
                CommunityModel(CommunityId(post.community.id), post.community.name, post.community.avatarUrl),
                PostContentModel(
                    post.content.title,
                    ContentBodyModel(post.content.body.preview,
                        cashedSpans.getOrPut(post.content.body.preview.orEmpty()) {
                            htmlToSpannableTransformer.transform(post.content.body.preview.orEmpty())
                        },
                        post.content.body.full,
                        cashedSpans.getOrPut(post.content.body.full.orEmpty()) {
                            htmlToSpannableTransformer.transform(post.content.body.full.orEmpty())
                        }),
                    post.content.metadata
                ),
                DiscussionVotesModel(
                    post.votes.hasUpVote,
                    post.votes.hasDownVote,
                    post.votes.upCount,
                    post.votes.downCount,
                    (voteEntity is QueryResult.Loading && voteEntity.originalQuery.power > 0),
                    (voteEntity is QueryResult.Loading && voteEntity.originalQuery.power < 0),
                    (voteEntity is QueryResult.Loading && voteEntity.originalQuery.power == 0.toShort())
                ),
                DiscussionCommentsCountModel(post.comments.count),
                DiscussionPayoutModel(post.payout.rShares),
                DiscussionMetadataModel(post.meta.time, post.meta.time.asElapsedTime())
            )
        }
        return out
    }
}

class CommentEntityToModelMapper(private val htmlToSpannableTransformer: HtmlToSpannableTransformer) :
    EntityToModelMapper<DiscussionRelatedEntities<CommentEntity>, CommentModel> {


    private val cashedValues =
        Collections.synchronizedMap(HashMap<DiscussionRelatedEntities<CommentEntity>, CommentModel>())
    private val cashedSpans = Collections.synchronizedMap(HashMap<String, CharSequence>())


    override suspend fun invoke(entity: DiscussionRelatedEntities<CommentEntity>): CommentModel {
        val comment = entity.discussionEntity

        val voteEntity = entity.voteStateEntity

        val out = cashedValues.getOrPut(entity) {
            CommentModel(
                DiscussionIdModel(comment.contentId.userId, comment.contentId.permlink, comment.contentId.refBlockNum),
                DiscussionAuthorModel(comment.author.userId, comment.author.username),
                CommentContentModel(
                    ContentBodyModel(comment.content.body.preview,
                        cashedSpans.getOrPut(comment.content.body.preview.orEmpty()) {
                            htmlToSpannableTransformer.transform(comment.content.body.preview.orEmpty())
                        },
                        comment.content.body.full,
                        cashedSpans.getOrPut(comment.content.body.full.orEmpty()) {
                            htmlToSpannableTransformer.transform(comment.content.body.full.orEmpty())
                        }),
                    comment.content.metadata,
                    if (comment.parentCommentId != null) 1 else 0
                ),
                DiscussionVotesModel(
                    comment.votes.hasUpVote,
                    comment.votes.hasDownVote,
                    comment.votes.upCount,
                    comment.votes.downCount,
                    (voteEntity is QueryResult.Loading && voteEntity.originalQuery.power > 0),
                    (voteEntity is QueryResult.Loading && voteEntity.originalQuery.power < 0),
                    (voteEntity is QueryResult.Loading && voteEntity.originalQuery.power == 0.toShort())
                ),
                DiscussionPayoutModel(comment.payout.rShares),
                DiscussionIdModel(
                    comment.parentPostId.userId,
                    comment.parentPostId.permlink,
                    comment.parentPostId.refBlockNum
                ),
                comment.parentCommentId?.let {
                    DiscussionIdModel(it.userId, it.permlink, it.refBlockNum)
                },
                DiscussionMetadataModel(
                    comment.meta.time, comment.meta.time.asElapsedTime()
                )
            )
        }
        return out
    }
}


class PostFeedEntityToModelMapper(private val postMapper: EntityToModelMapper<DiscussionRelatedEntities<PostEntity>, PostModel>) :
    EntityToModelMapper<FeedRelatedEntities<PostEntity>, DiscussionsFeed<PostModel>> {

    override suspend fun invoke(entity: FeedRelatedEntities<PostEntity>): DiscussionsFeed<PostModel> {
        val posts = entity.feed.discussions
        val votes = entity.votes.values.associateBy { it.originalQuery.discussionIdEntity }

        return DiscussionsFeed(posts
            .map { postEntity ->
                DiscussionRelatedEntities(postEntity, votes[postEntity.contentId])
            }
            .map { postRelatedEntities -> postMapper(postRelatedEntities) })
    }
}


class CommentsFeedEntityToModelMapper(private val commentsMapper: EntityToModelMapper<DiscussionRelatedEntities<CommentEntity>, CommentModel>) :
    EntityToModelMapper<FeedRelatedEntities<CommentEntity>, DiscussionsFeed<CommentModel>> {

    override suspend fun invoke(entity: FeedRelatedEntities<CommentEntity>): DiscussionsFeed<CommentModel> {
        val comments = entity.feed.discussions
        val votes = entity.votes.values.associateBy { it.originalQuery.discussionIdEntity }

        return DiscussionsFeed(comments
            .map { commentEntity ->
                DiscussionRelatedEntities(commentEntity, votes[commentEntity.contentId])

            }
            .map { postRelatedEntities -> commentsMapper(postRelatedEntities) })
    }
}


class VoteRequestEntityToModelMapper : EntityToModelMapper<VoteRequestEntity, VoteRequestModel> {
    private val cash = Collections.synchronizedMap(HashMap<VoteRequestEntity, VoteRequestModel>())

    override suspend fun invoke(entity: VoteRequestEntity): VoteRequestModel {
        return cash.getOrPut(entity) {
            when (entity) {
                is VoteRequestEntity.VoteForAPostRequestEntity -> VoteRequestModel.VoteForPostRequest(
                    entity.power,
                    DiscussionIdModel(
                        entity.discussionIdEntity.userId,
                        entity.discussionIdEntity.permlink,
                        entity.discussionIdEntity.refBlockNum
                    )
                )
                is VoteRequestEntity.VoteForACommentRequestEntity -> VoteRequestModel.VoteForComentRequest(
                    entity.power,
                    DiscussionIdModel(
                        entity.discussionIdEntity.userId,
                        entity.discussionIdEntity.permlink,
                        entity.discussionIdEntity.refBlockNum
                    )
                )
            }
        }
    }
}


