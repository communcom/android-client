package io.golos.domain.mappers.new_mappers

import io.golos.commun4j.model.DiscussionVotes
import io.golos.domain.interactors.model.DiscussionVotesModel

fun DiscussionVotes.map(): DiscussionVotesModel =
    DiscussionVotesModel(
        hasUpVote = this.upCount > 0,
        hasDownVote = this.downCount > 0,
        upCount = this.upCount,
        downCount = this.downCount)
