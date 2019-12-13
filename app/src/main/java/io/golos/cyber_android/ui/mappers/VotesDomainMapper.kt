package io.golos.cyber_android.ui.mappers

import io.golos.cyber_android.ui.dto.Post
import io.golos.domain.dto.PostDomain
import io.golos.domain.dto.VotesDomain

fun VotesDomain.mapToVotes(): Post.Votes {
    return Post.Votes(
        this.downCount,
        this.upCount,
        this.hasUpVote,
        this.hasDownVote
    )
}