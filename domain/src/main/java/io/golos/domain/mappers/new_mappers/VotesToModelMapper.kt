package io.golos.domain.mappers.new_mappers

import io.golos.commun4j.model.DiscussionVotes
import io.golos.domain.dto.VotesDomain
import io.golos.domain.use_cases.model.DiscussionVotesModel

fun DiscussionVotes.mapToDiscussionVotesModel(): DiscussionVotesModel =
    DiscussionVotesModel(
        hasUpVote = false,
        hasDownVote = false,
        upCount = this.upCount,
        downCount = this.downCount)

fun DiscussionVotes.mapToVotesDomain(): VotesDomain =
    VotesDomain(
        hasUpVote = this.hasUpVote!!,
        hasDownVote = this.hasDownVote!!,
        upCount = this.upCount,
        downCount = this.downCount
    )
