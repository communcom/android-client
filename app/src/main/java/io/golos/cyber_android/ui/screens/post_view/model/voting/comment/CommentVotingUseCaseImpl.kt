package io.golos.cyber_android.ui.screens.post_view.model.voting.comment

import io.golos.cyber_android.ui.screens.post_view.model.post_list_data_source.PostListDataSourceComments
import io.golos.use_cases.voting.VotingUseCaseImplBase
import io.golos.domain.DispatchersProvider
import io.golos.domain.dto.CommentDomain
import io.golos.domain.dto.CommunityIdDomain
import io.golos.domain.dto.UserIdDomain
import io.golos.domain.dto.VotesDomain
import io.golos.domain.repositories.DiscussionRepository
import io.golos.domain.use_cases.model.DiscussionVotesModel
import javax.inject.Inject

class CommentVotingUseCaseImpl
@Inject
constructor(
    dispatchersProvider: DispatchersProvider,
    discussionRepository: DiscussionRepository,
    private val postListDataSource: PostListDataSourceComments
) : VotingUseCaseImplBase(
    dispatchersProvider,
    discussionRepository
), CommentVotingUseCase {

    private lateinit var comment: CommentDomain

    override fun getCurrentVotes(): VotesDomain = comment.votes

    override suspend fun setCurrentVotes(votes: VotesDomain) {
        val oldVotes = getCurrentVotes()

        comment = comment.copy(
            votes = comment.votes.copy(
                hasUpVote = votes.hasUpVote,
                hasDownVote = votes.hasDownVote,
                downCount = votes.downCount,
                upCount = votes.upCount))

        postListDataSource.updateCommentVoteStatus(
            comment.contentId,
            votes.hasUpVote,
            votes.hasDownVote,
            (votes.upCount-votes.downCount)-(oldVotes.upCount-oldVotes.downCount))
    }

    override suspend fun upVote(comment: CommentDomain, communityId: CommunityIdDomain, userId: UserIdDomain, permlink: String): CommentDomain {
        this.comment = comment
        upVote(communityId, userId, permlink)
        return this.comment
    }

    override suspend fun downVote(comment: CommentDomain, communityId: CommunityIdDomain, userId: UserIdDomain, permlink: String): CommentDomain {
        this.comment = comment
        downVote(communityId, userId, permlink)
        return this.comment
    }

    private fun DiscussionVotesModel.map(): VotesDomain = VotesDomain(downCount, upCount, hasUpVote, hasDownVote)
}