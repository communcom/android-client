package io.golos.data.mappers

import io.golos.commun4j.model.DiscussionVotes
import io.golos.domain.dto.VotesDomain

fun DiscussionVotes.mapToVotesDomain(): VotesDomain {
    return VotesDomain(
        this.downCount,
        this.upCount,
        this.hasUpVote ?: false,
        this.hasDownVote ?: false
    )
}