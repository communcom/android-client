package io.golos.data.api.discussions

import io.golos.domain.commun_entities.CommentDiscussionRaw
import io.golos.domain.commun_entities.PostDiscussionRaw

object DataStorage {
    // Posts...
    val posts = mutableListOf<PostDiscussionRaw>()

    // ... and their comments (value of permlink as a key)
    val comments = mutableMapOf<String, List<CommentDiscussionRaw>>()
}