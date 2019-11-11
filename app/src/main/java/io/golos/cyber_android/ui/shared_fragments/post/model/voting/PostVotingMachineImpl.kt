package io.golos.cyber_android.ui.shared_fragments.post.model.voting

import io.golos.cyber_android.ui.shared_fragments.post.model.post_list_data_source.PostListDataSourcePostControls
import io.golos.data.repositories.vote.VoteRepository
import io.golos.domain.DispatchersProvider
import io.golos.domain.use_cases.model.DiscussionIdModel
import javax.inject.Inject

class PostVotingMachineImpl
@Inject
constructor(
    dispatchersProvider: DispatchersProvider,
    voteRepository: VoteRepository,
    private val postListDataSource: PostListDataSourcePostControls,
    postId: DiscussionIdModel
): VotingMachineImpl(
    dispatchersProvider,
    voteRepository,
    postId
) {
    override suspend fun updateVoteInPostList(isUpVoteActive: Boolean?, isDownVoteActive: Boolean?, voteBalanceDelta: Long) {
        postListDataSource.updatePostVoteStatus(isUpVoteActive, isDownVoteActive, voteBalanceDelta)
    }
}