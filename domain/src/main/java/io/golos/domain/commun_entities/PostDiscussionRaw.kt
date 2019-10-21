package io.golos.domain.commun_entities

import io.golos.commun4j.model.DiscussionAuthor
import io.golos.commun4j.model.DiscussionId
import io.golos.commun4j.model.DiscussionMetadata
import io.golos.commun4j.model.DiscussionVotes
import io.golos.commun4j.services.model.CyberCommunity

/**
 * Supposed entity for posts from API
 */
data class PostDiscussionRaw(
    val content: String,
    val votes: DiscussionVotes,
    val meta: DiscussionMetadata,

    val contentId: DiscussionId,

    val community: CyberCommunity,

    val author: DiscussionAuthor,

    val childCount: Long
)