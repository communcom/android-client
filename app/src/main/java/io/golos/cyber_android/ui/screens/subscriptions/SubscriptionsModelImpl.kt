package io.golos.cyber_android.ui.screens.subscriptions

import io.golos.cyber_android.ui.common.mvvm.model.ModelBase
import io.golos.cyber_android.ui.common.mvvm.model.ModelBaseImpl
import io.golos.domain.interactors.community.GetCommunitiesUseCase
import io.golos.domain.interactors.community.GetRecommendedCommunitiesUseCase
import io.golos.domain.interactors.community.SubscribeToCommunityUseCase
import io.golos.domain.interactors.community.UnsubscribeToCommunityUseCase
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