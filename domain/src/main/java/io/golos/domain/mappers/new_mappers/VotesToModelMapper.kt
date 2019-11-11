package io.golos.domain.mappers.new_mappers

import io.golos.commun4j.model.DiscussionVotes
import io.golos.domain.use_cases.model.DiscussionVotesModel

fun DiscussionVotes.map(): DiscussionVotesModel =
    DiscussionVotesModel(
        hasUpVote = false,
        hasDownVote = false,
        upCount = this.upCount,
        downCount = this.downCount)
