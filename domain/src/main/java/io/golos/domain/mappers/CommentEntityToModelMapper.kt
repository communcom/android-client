package io.golos.domain.mappers

import io.golos.domain.HtmlToSpannableTransformer
import io.golos.domain.dependency_injection.scopes.ApplicationScope
import io.golos.domain.dto.CommentEntity
import io.golos.domain.dto.DiscussionRelatedEntities
import io.golos.domain.extensions.asElapsedTime
import io.golos.domain.use_cases.model.*
import java.util.*
import javax.inject.Inject
import kotlin.collections.HashMap

interface CommentEntityToModelMapper : EntityToModelMapper<DiscussionRelatedEntities<CommentEntity>, CommentModel>

@ApplicationScope
class CommentEntityToModelMapperImpl
@Inject
constructor(
    private val htmlToSpannableTransformer: HtmlToSpannableTransformer
) : CommentEntityToModelMapper {

    private val cashedValues = Collections.synchronizedMap(HashMap<DiscussionRelatedEntities<CommentEntity>, CommentModel>())
    private val cashedSpans = Collections.synchronizedMap(HashMap<String, CharSequence>())

    override fun map(entity: DiscussionRelatedEntities<CommentEntity>): CommentModel {
        val comment = entity.discussionEntity

        val voteEntity = entity.voteStateEntity

        val out = cashedValues.getOrPut(entity) {
            CommentModel(
                DiscussionIdModel(comment.contentId.userId, comment.contentId.permlink),
                DiscussionAuthorModel(
                    comment.author.userId,
                    comment.author.username,
                    comment.author.avatarUrl
                ),
                CommentContentModel(
                    ContentBodyModel(
                        comment.content.body.postBlock
                    ),
                    if (comment.parentCommentId != null) 1 else 0
                ),
                DiscussionVotesModel(
                    comment.votes.hasUpVote,
                    comment.votes.hasDownVote,
                    comment.votes.upCount,
                    comment.votes.downCount
                ),
                DiscussionPayoutModel(),
                comment.parentCommentId?.let {
                    DiscussionIdModel(it.userId, it.permlink)
                },
                DiscussionMetadataModel(
                    comment.meta.time, comment.meta.time.asElapsedTime()
                ),
                DiscussionStatsModel(comment.stats.rShares, comment.stats.viewsCount),
                0L,
                listOf()
            )
        }
        return out
    }
}