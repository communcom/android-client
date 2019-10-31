package io.golos.cyber_android.ui.shared_fragments.post.model.voting

import io.golos.cyber_android.ui.shared_fragments.post.model.post_list_data_source.PostListDataSourceComments
import io.golos.data.repositories.vote.VoteRepository
import io.golos.domain.DispatchersProvider
import io.golos.domain.interactors.model.DiscussionIdModel

class CommentVotingMachineImpl
constructor(
    dispatchersProvider: DispatchersProvider,
    voteRepository: VoteRepository,
    private val postListDataSource: PostListDataSourceComments,
    private val commentId: DiscussionIdModel
): VotingMachineImpl(
    dispatchersProvider,
    voteRepository,
    commentId
) {
    override suspend fun updateVoteInPostList(isUpVoteActive: Boolean?, isDownVoteActive: Boolean?, voteBalanceDelta: Long) {
        postListDataSource.updateCommentVoteStatus(commentId, isUpVoteActive, isDownVoteActive, voteBalanceDelta)
    }
}