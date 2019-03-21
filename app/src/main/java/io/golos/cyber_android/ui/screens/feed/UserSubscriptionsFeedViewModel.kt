package io.golos.cyber_android.ui.screens.feed

import io.golos.domain.interactors.feed.UserSubscriptionsFeedUseCase
import io.golos.domain.model.UserSubscriptionsFeedUpdateRequest

class UserSubscriptionsFeedViewModel(userSubscriptionFeedUseCase: UserSubscriptionsFeedUseCase) :
    FeedPageTabViewModel<UserSubscriptionsFeedUpdateRequest>(userSubscriptionFeedUseCase)