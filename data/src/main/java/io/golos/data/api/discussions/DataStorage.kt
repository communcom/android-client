package io.golos.data.api.discussions

import io.golos.domain.commun_entities.CommentDiscussionRaw
import io.golos.domain.commun_entities.PostDiscussionRaw

object DataStorage {
    // Posts...
    val posts = mutableListOf<PostDiscussionRaw>()

    // ... and their comments (value of a post permlink as a key)
    val commentsForPost = mutableMapOf<String, MutableList<CommentDiscussionRaw>>()

    // list of first-level comments (key is permlink of comment)
    val comments = mutableMapOf<String, CommentDiscussionRaw>()
}