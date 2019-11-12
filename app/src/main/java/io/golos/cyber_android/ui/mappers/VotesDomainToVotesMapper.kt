package io.golos.cyber_android.ui.mappers

import io.golos.cyber_android.ui.dto.Post
import io.golos.domain.dto.PostDomain

class VotesDomainToVotesMapper : Function1<PostDomain.VotesDomain, Post.Votes> {

    override fun invoke(votesDomain: PostDomain.VotesDomain): Post.Votes {
        return Post.Votes(
            votesDomain.downCount,
            votesDomain.upCount,
            votesDomain.hasUpVote,
            votesDomain.hasDownVote
        )
    }
}