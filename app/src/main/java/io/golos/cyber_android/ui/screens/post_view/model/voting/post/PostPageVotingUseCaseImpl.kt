package io.golos.cyber_android.ui.screens.post_view.model.voting.post

import io.golos.cyber_android.ui.screens.post_view.model.post_list_data_source.PostListDataSource
import io.golos.use_cases.voting.VotingUseCaseImplBase
import io.golos.domain.DispatchersProvider
import io.golos.domain.dto.PostDomain
import io.golos.domain.dto.VotesDomain
import io.golos.domain.repositories.DiscussionRepository
import javax.inject.Inject

class PostPageVotingUseCaseImpl
@Inject
constructor(
    dispatchersProvider: DispatchersProvider,
    discussionRepository: DiscussionRepository,
    private val postListDataSource: PostListDataSource
) : VotingUseCaseImplBase(
    dispatchersProvider,
    discussionRepository
), PostPageVotingUseCase {

    private lateinit var post: PostDomain

    override fun getCurrentVotes(): VotesDomain = post.votes

    override suspend fun setCurrentVotes(votes: VotesDomain) {
        post = post.copy(votes = votes)
        postListDataSource.createOrUpdatePostData(post)
    }

    override suspend fun upVote(post: PostDomain, communityId: String, userId: String, permlink: String): PostDomain {
        this.post = post
        upVote(communityId, userId, permlink)
        return this.post
    }

    override suspend fun downVote(post: PostDomain, communityId: String, userId: String, permlink: String): PostDomain {
        this.post = post
        downVote(communityId, userId, permlink)
        return this.post
    }
}