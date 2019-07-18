package io.golos.domain.rules

import io.golos.cyber4j.model.CyberDiscussion
import io.golos.cyber4j.model.DiscussionsResult
import io.golos.cyber4j.model.ImageRow
import io.golos.cyber4j.model.TextRow
import io.golos.domain.entities.*
import io.golos.domain.requestmodel.FeedUpdateRequest
import java.math.BigInteger
import javax.inject.Inject

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-03-13.
 */
data class FeedUpdateRequestsWithResult<Q : FeedUpdateRequest>(
    val feedRequest: Q,
    val discussionsResult: DiscussionsResult
)

class CyberPostToEntityMapper
@Inject
constructor() : CyberToEntityMapper<CyberDiscussion, PostEntity> {

    override suspend fun invoke(cyberObject: CyberDiscussion): PostEntity {
        return PostEntity(
            DiscussionIdEntity(
                cyberObject.contentId.userId,
                cyberObject.contentId.permlink
            ),
            DiscussionAuthorEntity(
                CyberUser(cyberObject.author?.userId?.name ?: "unknown"),
                cyberObject.author?.username ?: "unknown",
                cyberObject.author?.avatarUrl ?: ""
            ),
            CommunityEntity(
                cyberObject.community!!.id,
                cyberObject.community!!.name,
                cyberObject.community!!.getAvatarUrl
            ),
            PostContent(
                cyberObject.content.title!!,
                ContentBody(
                    cyberObject.content.body.preview ?: "",
                    cyberObject.content.body.mobile.orEmpty().map {
                        when (it) {
                            is ImageRow -> ImageRowEntity(it.src)
                            is TextRow -> TextRowEntity(it.content)
                        }
                    }
                    ,
                    cyberObject.content.embeds
                        .map {
                            EmbedEntity(
                                it.result?.type ?: "",
                                it.result?.title ?: "",
                                it.result?.url ?: "",
                                it.result?.author ?: "",
                                it.result?.provider_name ?: "",
                                it.result?.html ?: ""
                            )
                        },
                    cyberObject.content.body.mobilePreview.orEmpty().map {
                        when (it) {
                            is ImageRow -> ImageRowEntity(it.src)
                            is TextRow -> TextRowEntity(it.content)
                        }
                    }),
                cyberObject.content.tags?.map { TagEntity(it) } ?: emptyList()
            ),
            DiscussionVotes(
                cyberObject.votes.hasUpVote,
                cyberObject.votes.hasDownVote,
                cyberObject.votes.upCount,
                cyberObject.votes.downCount
            ),
            DiscussionCommentsCount(cyberObject.stats!!.commentsCount!!),
            DiscussionPayout(cyberObject.stats?.rShares.orZero()),
            DiscussionMetadata(cyberObject.meta.time)
        )
    }
}


class CyberFeedToEntityMapper
@Inject
constructor(val postMapper: CyberToEntityMapper<CyberDiscussion, PostEntity>) :
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

class CyberCommentToEntityMapper
@Inject
constructor() : CyberToEntityMapper<CyberDiscussion, CommentEntity> {

    override suspend fun invoke(cyberObject: CyberDiscussion): CommentEntity {
        return CommentEntity(
            DiscussionIdEntity(
                cyberObject.contentId.userId,
                cyberObject.contentId.permlink
            ),
            DiscussionAuthorEntity(
                CyberUser(cyberObject.author?.userId?.name ?: "unknown"),
                cyberObject.author?.username ?: "unknown",
                cyberObject.author?.avatarUrl ?: ""
            ),
            CommentContent(
                ContentBody("",
                    cyberObject.content.body.mobile.orEmpty()
                        .map {
                            when (it) {
                                is ImageRow -> ImageRowEntity(it.src)
                                is TextRow -> TextRowEntity(it.content)
                            }
                        }, cyberObject.content.embeds
                        .map {
                            EmbedEntity(
                                it.result?.type ?: "",
                                it.result?.title ?: "",
                                it.result?.url ?: "",
                                it.result?.author ?: "",
                                it.result?.provider_name ?: "",
                                it.result?.html ?: ""
                            )
                        },
                    cyberObject.content.body.mobilePreview.orEmpty()
                        .map {
                            when (it) {
                                is ImageRow -> ImageRowEntity(it.src)
                                is TextRow -> TextRowEntity(it.content)
                            }
                        }
                )
            ),
            DiscussionVotes(
                cyberObject.votes.hasUpVote,
                cyberObject.votes.hasDownVote,
                cyberObject.votes.upCount,
                cyberObject.votes.downCount
            ),
            DiscussionPayout(cyberObject.stats?.rShares.orZero()),
            cyberObject.parent!!.post!!.contentId.let {
                DiscussionIdEntity(it.userId, it.permlink)
            },
            cyberObject.parent?.comment?.contentId?.let {
                DiscussionIdEntity(it.userId, it.permlink)
            },
            DiscussionMetadata(cyberObject.meta.time)
        )
    }
}

class CyberCommentsToEntityMapper
@Inject
constructor(val postMapper: CyberToEntityMapper<CyberDiscussion, CommentEntity>) :
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

/**
 * TODO remove later
 */
private fun BigInteger?.orZero() = this ?: BigInteger("0")