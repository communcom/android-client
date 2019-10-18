package io.golos.cyber_android.ui.shared_fragments.post.model.voting

/**
 * User's actions
 */
enum class VotingEvent {
    UP_VOTE,
    DOWN_VOTE
}

enum class VotingState {
    NOT_VOTING,
    NOT_VOTED,

    UP_VOTING,
    UP_VOTED,

    DOWN_VOTING,
    DOWN_VOTED
}