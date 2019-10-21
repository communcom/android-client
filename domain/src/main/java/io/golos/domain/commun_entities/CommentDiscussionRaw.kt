package io.golos.domain.commun_entities

import io.golos.commun4j.model.DiscussionAuthor
import io.golos.commun4j.model.DiscussionId
import io.golos.commun4j.model.DiscussionMetadata
import io.golos.commun4j.model.DiscussionVotes

/**
 * Supposed entity for comments from API
 */
data class CommentDiscussionRaw(
    val content: String,
    val votes: DiscussionVotes,
    val meta: DiscussionMetadata,

    val contentId: DiscussionId,
    val parentContentId: DiscussionId?,

    val author: DiscussionAuthor,

    val childTotal: Long,

    val child: List<CommentDiscussionRaw>
)
