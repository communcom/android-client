package io.golos.cyber_android.ui.screens.subscriptions

import io.golos.cyber_android.ui.shared.mvvm.model.ModelBase
import io.golos.cyber_android.ui.shared.mvvm.model.ModelBaseImpl
import io.golos.domain.use_cases.community.GetCommunitiesUseCase
import io.golos.domain.use_cases.community.GetRecommendedCommunitiesUseCase
import io.golos.domain.use_cases.community.SubscribeToCommunityUseCase
import io.golos.domain.use_cases.community.UnsubscribeToCommunityUseCase
import javax.inject.Inject

class SubscriptionsModelImpl @Inject constructor(
    getCommunitiesUseCase: GetCommunitiesUseCase,
    getRecommendedCommunitiesUseCase: GetRecommendedCommunitiesUseCase,
    subscribeToCommunityUseCase: SubscribeToCommunityUseCase,
    unsubscribeToCommunityUseCase: UnsubscribeToCommunityUseCase
) :
    ModelBase,
    ModelBaseImpl(),
    SubscriptionsModel,
    GetCommunitiesUseCase by getCommunitiesUseCase,
    GetRecommendedCommunitiesUseCase by getRecommendedCommunitiesUseCase,
    SubscribeToCommunityUseCase by subscribeToCommunityUseCase,
    UnsubscribeToCommunityUseCase by unsubscribeToCommunityUseCase