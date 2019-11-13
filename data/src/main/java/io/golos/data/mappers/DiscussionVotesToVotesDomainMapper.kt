package io.golos.data.mappers

import io.golos.commun4j.model.DiscussionVotes
import io.golos.domain.dto.PostDomain

class DiscussionVotesToVotesDomainMapper : Function1<DiscussionVotes, PostDomain.VotesDomain> {

    override fun invoke(discussionVotes: DiscussionVotes): PostDomain.VotesDomain {
        return PostDomain.VotesDomain(discussionVotes.downCount,
            discussionVotes.upCount,
            discussionVotes.hasUpVote ?: false,
            discussionVotes.hasDownVote ?: false)
    }
}