package io.golos.cyber_android.ui.screens.feed

import io.golos.domain.interactors.action.VoteUseCase
import io.golos.domain.interactors.feed.UserSubscriptionsFeedUseCase
import io.golos.domain.interactors.publish.DiscussionPosterUseCase
import io.golos.domain.interactors.sign.SignInUseCase
import io.golos.domain.interactors.user.UserMetadataUseCase
import io.golos.domain.requestmodel.PostFeedUpdateRequest

class UserSubscriptionsFeedViewModel(useCase: UserSubscriptionsFeedUseCase,
                                     voteUseCase: VoteUseCase,
                                     posterUseCase: DiscussionPosterUseCase,
                                     userMetadataUseCase: UserMetadataUseCase,
                                     signInUseCase: SignInUseCase
) :
    FeedPageTabViewModel<PostFeedUpdateRequest>(useCase, voteUseCase, posterUseCase, signInUseCase, userMetadataUseCase)