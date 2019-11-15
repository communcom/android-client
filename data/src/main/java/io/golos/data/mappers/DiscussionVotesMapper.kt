package io.golos.data.mappers

import io.golos.commun4j.model.DiscussionVotes
import io.golos.domain.dto.PostDomain

fun DiscussionVotes.mapToVotesDomain(): PostDomain.VotesDomain {
    return PostDomain.VotesDomain(
        this.downCount,
        this.upCount,
        this.hasUpVote ?: false,
        this.hasDownVote ?: false
    )
}