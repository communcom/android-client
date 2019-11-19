package io.golos.cyber_android.ui.screens.profile.old_profile.posts

import io.golos.cyber_android.ui.screens.main_activity.feed.FeedPageTabViewModel
import io.golos.domain.dto.DiscussionsSort
import io.golos.domain.dto.PostEntity
import io.golos.domain.use_cases.action.VoteUseCase
import io.golos.domain.use_cases.feed.AbstractFeedUseCase
import io.golos.domain.use_cases.model.PostModel
import io.golos.domain.use_cases.publish.DiscussionPosterUseCase
import io.golos.domain.use_cases.sign.SignInUseCase
import io.golos.domain.requestmodel.PostFeedUpdateRequest
import javax.inject.Inject

class UserPostsFeedViewModel
@Inject
constructor(
    feedUseCase: AbstractFeedUseCase<PostFeedUpdateRequest, PostEntity, PostModel>,
    voteUseCase: VoteUseCase,
    posterUseCase: DiscussionPosterUseCase,
    signInUseCase: SignInUseCase
) : FeedPageTabViewModel<PostFeedUpdateRequest>(feedUseCase, voteUseCase, posterUseCase, signInUseCase) {

    override fun getFeedSort(): DiscussionsSort? = DiscussionsSort.FROM_NEW_TO_OLD
}