package io.golos.cyber_android.ui.screens.feed

import io.golos.domain.entities.PostEntity
import io.golos.domain.interactors.action.VoteUseCase
import io.golos.domain.interactors.feed.AbstractFeedUseCase
import io.golos.domain.interactors.feed.UserSubscriptionsFeedUseCase
import io.golos.domain.interactors.model.PostModel
import io.golos.domain.interactors.publish.DiscussionPosterUseCase
import io.golos.domain.interactors.sign.SignInUseCase
import io.golos.domain.interactors.user.UserMetadataUseCase
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