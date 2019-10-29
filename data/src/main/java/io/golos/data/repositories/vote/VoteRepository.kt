package io.golos.data.repositories.vote

import io.golos.domain.interactors.model.DiscussionIdModel

interface VoteRepository {
    /**
     * [entityId] post or comment
     */
    fun upVote(entityId: DiscussionIdModel)

    /**
     * [entityId] post or comment
     */
    fun downVote(entityId: DiscussionIdModel)

    /**
     * [entityId] post or comment
     */
    fun unVote(entityId: DiscussionIdModel)
}