package io.golos.cyber_android.ui.screens.post_view.model.voting

/**
 * User's actions
 */
@Deprecated("Not need use, use Domain model")
enum class VotingEvent {
    UP_VOTE,
    DOWN_VOTE
}

@Deprecated("Not need use, use Domain model")
enum class VotingState {
    NOT_VOTING,
    NOT_VOTED,

    UP_VOTING,
    UP_VOTED,

    DOWN_VOTING,
    DOWN_VOTED
}