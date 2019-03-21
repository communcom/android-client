package io.golos.cyber_android.ui.screens.feed

import io.golos.domain.interactors.feed.UserPostFeedUseCase
import io.golos.domain.interactors.feed.UserSubscriptionsFeedUseCase
import io.golos.domain.model.UserPostsUpdateRequest
import io.golos.domain.model.UserSubscriptionsFeedUpdateRequest

class UserSubscriptionsFeedFeedViewModel(useCase: UserPostFeedUseCase) :
    FeedPageTabViewModel<UserPostsUpdateRequest>(useCase)