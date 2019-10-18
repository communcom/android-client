package io.golos.data.repositories.vote

import io.golos.domain.interactors.model.DiscussionIdModel

interface VoteRepository {
    fun upVote(postId: DiscussionIdModel)

    fun downVote(postId: DiscussionIdModel)

    fun unVote(postId: DiscussionIdModel)
}