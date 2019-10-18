package io.golos.cyber_android.ui.shared_fragments.post.model.voting

import io.golos.domain.interactors.model.DiscussionVotesModel

interface VotingMachine {
    suspend fun processEvent(event: VotingEvent, votesModel: DiscussionVotesModel): DiscussionVotesModel
}