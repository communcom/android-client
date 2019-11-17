package io.golos.cyber_android.ui.mappers

import io.golos.cyber_android.ui.dto.Post
import io.golos.domain.dto.PostDomain

fun PostDomain.VotesDomain.mapToVotes(): Post.Votes {
    return Post.Votes(
        this.downCount,
        this.upCount,
        this.hasUpVote,
        this.hasDownVote
    )
}