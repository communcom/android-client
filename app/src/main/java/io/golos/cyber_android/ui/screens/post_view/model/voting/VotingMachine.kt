package io.golos.cyber_android.ui.screens.post_view.model.voting

import io.golos.domain.use_cases.model.DiscussionVotesModel

@Deprecated("Not need use, use Domain model")
interface VotingMachine {
    suspend fun processEvent(event: VotingEvent, votesModel: DiscussionVotesModel): DiscussionVotesModel
}