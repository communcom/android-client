package io.golos.cyber_android.ui.screens.subscriptions

import io.golos.cyber_android.ui.common.mvvm.model.ModelBase
import io.golos.domain.interactors.community.GetCommunitiesUseCase
import io.golos.domain.interactors.community.GetRecommendedCommunitiesUseCase
import io.golos.domain.interactors.community.SubscribeToCommunityUseCase
import io.golos.domain.interactors.community.UnsubscribeToCommunityUseCase

interface SubscriptionsModel : ModelBase,
    GetCommunitiesUseCase,
    GetRecommendedCommunitiesUseCase,
    SubscribeToCommunityUseCase,
    UnsubscribeToCommunityUseCase