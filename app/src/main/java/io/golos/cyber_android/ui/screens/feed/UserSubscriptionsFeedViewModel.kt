package io.golos.cyber_android.ui.screens.feed

import io.golos.domain.dto.PostEntity
import io.golos.domain.use_cases.action.VoteUseCase
import io.golos.domain.use_cases.feed.AbstractFeedUseCase
import io.golos.domain.use_cases.feed.UserSubscriptionsFeedUseCase
import io.golos.domain.use_cases.model.PostModel
import io.golos.domain.use_cases.publish.DiscussionPosterUseCase
import io.golos.domain.use_cases.sign.SignInUseCase
import io.golos.domain.use_cases.user.UserMetadataUseCase
import io.golos.domain.requestmodel.PostFeedUpdateRequest
import javax.inject.Inject

@Suppress("UNCHECKED_CAST")
class UserSubscriptionsFeedViewModel
@Inject
constructor(
    useCase: UserSubscriptionsFeedUseCase,
    voteUseCase: VoteUseCase,
    posterUseCase: DiscussionPosterUseCase,
    userMetadataUseCase: UserMetadataUseCase,
    signInUseCase: SignInUseCase
) : FeedPageTabViewModel<PostFeedUpdateRequest>(
    useCase as AbstractFeedUseCase<PostFeedUpdateRequest, PostEntity, PostModel>,
    voteUseCase,
    posterUseCase,
    signInUseCase,
    userMetadataUseCase)