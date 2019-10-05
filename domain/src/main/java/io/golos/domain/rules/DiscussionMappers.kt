package io.golos.domain.rules

import io.golos.commun4j.model.CyberDiscussion
import io.golos.commun4j.model.DiscussionsResult
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

    @Suppress("CAST_NEVER_SUCCEEDS")
    override suspend fun invoke(cyberObject: CyberDiscussion): PostEntity {
        return PostEntity(
            DiscussionIdEntity(
                cyberObject.contentId.userId,
                cyberObject.contentId.permlink
            ),
            DiscussionAuthorEntity(
                CyberUser(cyberObject.author.userId.name ?: "unknown"),
                cyberObject.author.username ?: "unknown",
                cyberObject.author.avatarUrl ?: ""
            ),
            CommunityEntity(
                cyberObject.community.id,
                cyberObject.community.name!!,
                cyberObject.community.avatarUrl
            ),
            PostContent(
                cyberObject.content.attributes.title,
                ContentBody(
                    "{ \"id\": 1, \"type\": \"post\", \"attributes\": { \"version\": \"1.0\", \"title\": \"Сказка про царя\", \"type\": \"basic\" }, \"content\": [ { \"id\": 2, \"type\": \"paragraph\", \"content\": [ { \"id\": 3, \"type\": \"text\", \"content\": \"Много лет тому назад, \" }, { \"id\": 4, \"type\": \"mention\", \"content\": \"Царь\" }, { \"id\": 5, \"type\": \"text\", \"content\": \" купил себе айпад. \" }, { \"id\": 6, \"type\": \"tag\", \"content\": \"с_той_поры_прошли_века\" }, { \"id\": 7, \"type\": \"link\", \"content\": \" , Люди \", \"attributes\": { \"url\": \"https://www.youtube.com/watch?v=UiYlRkVxC_4\" } }, { \"id\": 8, \"type\": \"link\", \"content\": \"помнят \", \"attributes\": { \"url\": \"https://assets.pixyblog.com/wp-content/uploads/sites/3/2018/10/JULIE-LONDON-51-copy-515x600.jpg\" } }, { \"id\": 9, \"type\": \"link\", \"content\": \"чудака.\", \"attributes\": { \"url\": \"https://diletant.media\" } } ] }, { \"id\": 10, \"type\": \"image\", \"content\": \"https://assets.pixyblog.com/wp-content/uploads/sites/3/2018/10/JULIE-LONDON-51-copy-515x600.jpg\", \"attributes\": { \"description\": \"Hi!\" } } ] }"
                ),
                listOf()
            ),
            DiscussionVotes(
                cyberObject.votes.upCount > 0,
                cyberObject.votes.downCount > 0,
                cyberObject.votes.upCount,
                cyberObject.votes.downCount
            ),
            DiscussionCommentsCount(0L),            // note[AS] temporary zero - it'll be in a future
            DiscussionPayout(),
            DiscussionMetadata(cyberObject.meta.creationTime),
            DiscussionStats(0.toBigInteger(), 0L)                  // note[AS] temporary zero - it'll be in a future
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
                CyberUser(cyberObject.author.userId.name ?: "unknown"),
                cyberObject.author.username ?: "unknown",
                cyberObject.author.avatarUrl ?: ""
            ),
            CommentContent(
                ContentBody("")
            ),
            DiscussionVotes(
                cyberObject.votes.upCount > 0,
                cyberObject.votes.downCount > 0,
                cyberObject.votes.upCount,
                cyberObject.votes.downCount
            ),
            DiscussionPayout(),
            null,                           // note[AS] it's an Id of a parent comment. Temporary null - it'll be in a future
            DiscussionMetadata(cyberObject.meta.creationTime),
            DiscussionStats(0.toBigInteger(), 0L)
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