package io.golos.cyber_android.ui.screens.subscriptions

import io.golos.cyber_android.ui.common.mvvm.model.ModelBase
import io.golos.domain.use_cases.community.GetCommunitiesUseCase
import io.golos.domain.use_cases.community.GetRecommendedCommunitiesUseCase
import io.golos.domain.use_cases.community.SubscribeToCommunityUseCase
import io.golos.domain.use_cases.community.UnsubscribeToCommunityUseCase

interface SubscriptionsModel : ModelBase,
    GetCommunitiesUseCase,
    GetRecommendedCommunitiesUseCase,
    SubscribeToCommunityUseCase,
    UnsubscribeToCommunityUseCase