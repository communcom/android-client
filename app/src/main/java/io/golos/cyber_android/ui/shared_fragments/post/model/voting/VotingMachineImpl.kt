package io.golos.cyber_android.ui.shared_fragments.post.model.voting

import io.golos.data.repositories.vote.VoteRepository
import io.golos.domain.DispatchersProvider
import io.golos.domain.use_cases.model.DiscussionIdModel
import io.golos.domain.use_cases.model.DiscussionVotesModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

abstract class VotingMachineImpl
constructor(
    private val dispatchersProvider: DispatchersProvider,
    private val voteRepository: VoteRepository,
    private val entityToProcess: DiscussionIdModel
): VotingMachine {
    private var currentState: VotingState = VotingState.NOT_VOTED

    /**
     * Process voting event end returns updated [DiscussionVotesModel]
     */
    override suspend fun processEvent(event: VotingEvent, votesModel: DiscussionVotesModel): DiscussionVotesModel {
        val stateFromModel = calculateState(votesModel)

        return when (currentState) {
            VotingState.NOT_VOTED -> {
                if(stateFromModel != currentState) {
                    currentState = stateFromModel
                    return processEvent(event, votesModel)
                }

                when (event) {
                    VotingEvent.UP_VOTE -> {
                        currentState = VotingState.UP_VOTING

                        apiCalls {
                            upVote()
                        }

                        updateVoteInPostList(true, null, 1)
                        currentState = VotingState.UP_VOTED

                        votesModel.copy(hasUpVote = true, hasDownVote = false, upCount = votesModel.upCount + 1)
                    }

                    VotingEvent.DOWN_VOTE -> {
                        currentState = VotingState.DOWN_VOTING

                        apiCalls {
                            downVote()
                        }

                        updateVoteInPostList(null, true, -1)
                        currentState = VotingState.DOWN_VOTED

                        votesModel.copy(hasUpVote = false, hasDownVote = true, upCount = votesModel.upCount - 1)
                    }
                }
            }

            VotingState.UP_VOTED -> {
                if(stateFromModel != currentState) {
                    currentState = stateFromModel
                    return processEvent(event, votesModel)
                }

                when (event) {
                    VotingEvent.UP_VOTE -> {
                        currentState = VotingState.NOT_VOTING

                        apiCalls {
                            unVote()
                        }

                        updateVoteInPostList(false, null, -1)
                        currentState = VotingState.NOT_VOTED

                        votesModel.copy(hasUpVote = false, hasDownVote = false, upCount = votesModel.upCount - 1)
                    }

                    VotingEvent.DOWN_VOTE -> {
                        currentState = VotingState.DOWN_VOTING

                        apiCalls {
                            unVote()
                            downVote()
                        }

                        updateVoteInPostList(false, true, -2)
                        currentState = VotingState.DOWN_VOTED

                        votesModel.copy(hasUpVote = false, hasDownVote = true, upCount = votesModel.upCount - 2)
                    }
                }
            }

            VotingState.DOWN_VOTED -> {
                if(stateFromModel != currentState) {
                    currentState = stateFromModel
                    return processEvent(event, votesModel)
                }

                when (event) {
                    VotingEvent.UP_VOTE -> {
                        currentState = VotingState.UP_VOTING

                        apiCalls {
                            unVote()
                            upVote()
                        }

                        updateVoteInPostList(true, false, 2)
                        currentState = VotingState.UP_VOTED

                        votesModel.copy(hasUpVote = true, hasDownVote = false, upCount = votesModel.upCount + 2)
                    }

                    VotingEvent.DOWN_VOTE -> {
                        currentState = VotingState.NOT_VOTING

                        apiCalls {
                            unVote()
                        }

                        updateVoteInPostList(null, false, 1)
                        currentState = VotingState.NOT_VOTED

                        votesModel.copy(hasUpVote = false, hasDownVote = false, upCount = votesModel.upCount + 1)
                    }
                }
            }

            VotingState.NOT_VOTING,
            VotingState.UP_VOTING,
            VotingState.DOWN_VOTING -> votesModel // Voting in progress - do nothing
        }
    }

    protected abstract suspend fun updateVoteInPostList(isUpVoteActive: Boolean?, isDownVoteActive: Boolean?, voteBalanceDelta: Long)

    private suspend fun apiCalls(action: () -> Unit) {
        withContext(dispatchersProvider.ioDispatcher) {
            delay(1000)
            action()
        }
    }

    private fun upVote() = voteRepository.upVote(entityToProcess)

    private fun downVote() = voteRepository.downVote(entityToProcess)

    private fun unVote() = voteRepository.unVote(entityToProcess)

    private fun calculateState(votesModel: DiscussionVotesModel): VotingState =
        when {
            votesModel.hasUpVote -> VotingState.UP_VOTED
            votesModel.hasDownVote -> VotingState.DOWN_VOTED
            else -> VotingState.NOT_VOTED
        }
}