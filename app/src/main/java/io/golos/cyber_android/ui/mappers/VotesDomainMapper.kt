package io.golos.cyber_android.ui.mappers

import io.golos.cyber_android.ui.dto.Votes
import io.golos.domain.dto.VotesDomain

fun VotesDomain.mapToVotes(): Votes {
    return Votes(
        this.downCount,
        this.upCount,
        this.hasUpVote,
        this.hasDownVote
    )
}