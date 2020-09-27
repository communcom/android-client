package io.golos.use_cases.voting

import io.golos.domain.DispatchersProvider
import io.golos.domain.dto.CommunityIdDomain
import io.golos.domain.dto.ContentIdDomain
import io.golos.domain.dto.UserIdDomain
import io.golos.domain.dto.VotesDomain
import io.golos.domain.repositories.DiscussionRepository
import kotlinx.coroutines.withContext
import timber.log.Timber

abstract class VotingUseCaseImplBase(private val dispatchersProvider: DispatchersProvider, private val discussionRepository: DiscussionRepository) : VotingUseCase {

    private var voteInProgress = false

    private lateinit var oldVotesState: VotesDomain

    override suspend fun upVote(communityId: CommunityIdDomain, userId: UserIdDomain, permlink: String) {
        if (voteInProgress) {
            return
        }
        voteInProgress = true

        val votes = getCurrentVotes()
        if (votes.hasUpVote) {
            voteInProgress = false
            return
        }

        oldVotesState = votes
        val newVotesState = calculateVotesForUpVote(oldVotesState)

        setCurrentVotes(newVotesState)

        try {
            withContext(dispatchersProvider.ioDispatcher) {
                discussionRepository.upVote(ContentIdDomain(communityId = communityId, permlink = permlink, userId = userId))
            }
        } catch (ex: Exception) {
            Timber.e(ex)
            setCurrentVotes(oldVotesState)
            throw ex
        } finally {
            voteInProgress = false
        }
    }

    override suspend fun downVote(communityId: CommunityIdDomain, userId: UserIdDomain, permlink: String) {
        if (voteInProgress) {
            return
        }
        voteInProgress = true

        val votes = getCurrentVotes()
        if (votes.hasDownVote) {
            voteInProgress = false
            return
        }

        oldVotesState = votes
        val newVotesState = calculateVotesForDownVote(oldVotesState)

        setCurrentVotes(newVotesState)

        try {
            withContext(dispatchersProvider.ioDispatcher) {
                discussionRepository.downVote(ContentIdDomain(communityId = communityId, permlink = permlink, userId = userId))
            }
        } catch (ex: Exception) {
            Timber.e(ex)
            setCurrentVotes(oldVotesState)
            throw ex
        } finally {
            voteInProgress = false
        }
    }
    override suspend fun unVote(communityId: CommunityIdDomain, userId: UserIdDomain, permlink: String) {
        if (voteInProgress) {
            return
        }
        voteInProgress = true

        val votes = getCurrentVotes()

        oldVotesState = votes
        val newVotesState = calculateNaturalVote(oldVotesState)
        setCurrentVotes(newVotesState)

        try {
            withContext(dispatchersProvider.ioDispatcher) {
                discussionRepository.unVote(ContentIdDomain(communityId = communityId, permlink = permlink, userId = userId))
            }
        } catch (ex: Exception) {
            Timber.e(ex)
            setCurrentVotes(oldVotesState)
            throw ex
        } finally {
            voteInProgress = false
        }
    }

    override suspend fun unVote(communityId: CommunityIdDomain, userId: UserIdDomain, permlink: String) {
        if (voteInProgress) {
            return
        }
        voteInProgress = true

        val votes = getCurrentVotes()

        oldVotesState = votes
        val newVotesState = calculateNaturalVote(oldVotesState)
        setCurrentVotes(newVotesState)

        try {
            withContext(dispatchersProvider.ioDispatcher) {
                discussionRepository.unVote(ContentIdDomain(communityId = communityId, permlink = permlink, userId = userId))
            }
        } catch (ex: Exception) {
            Timber.e(ex)
            setCurrentVotes(oldVotesState)
            throw ex
        } finally {
            voteInProgress = false
        }
    }

    abstract fun getCurrentVotes(): VotesDomain

    abstract suspend fun setCurrentVotes(votes: VotesDomain)

    /*   private fun calculateVotesForUpVote(old: VotesDomain): VotesDomain =
           old.copy(
               upCount = old.upCount + 1,
               hasDownVote = false,
               hasUpVote = true
           ) */
    private fun calculateVotesForUpVote(old: VotesDomain): VotesDomain =
        old.copy(
            upCount = old.upCount + 1,
            downCount = if (old.hasDownVote) old.downCount - 1 else old.downCount,
            hasUpVote = true,
            hasDownVote = false)

    private fun calculateVotesForDownVote(old: VotesDomain): VotesDomain =
        old.copy(
            downCount = old.downCount + 1,
            upCount = if (old.hasUpVote) old.upCount - 1 else old.upCount,
            hasUpVote = false,
            hasDownVote = true)

    private fun calculateNaturalVote(old: VotesDomain):VotesDomain {
        return if (old.hasUpVote) {
            old.copy(upCount = old.upCount - 1, downCount = if (old.hasDownVote) old.downCount - 1 else old.downCount, hasUpVote = false, hasDownVote = false)
        } else {
            old.copy(downCount = old.downCount - 1, upCount = if (old.hasUpVote) old.upCount - 1 else old.upCount, hasUpVote = false, hasDownVote = false)
        }
    }
}