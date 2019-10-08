package io.golos.cyber_android.ui.screens.subscriptions

import io.golos.cyber_android.ui.common.mvvm.model.ModelBase
import io.golos.cyber_android.ui.common.mvvm.model.ModelBaseImpl
import io.golos.domain.interactors.community.CheckSubscriptionsOnCommunitiesUseCase
import io.golos.domain.interactors.community.GetCommunitiesUseCase
import io.golos.domain.interactors.community.GetRecommendedCommunitiesUseCase
import javax.inject.Inject

class SubscriptionsModelImpl @Inject constructor(
    checkSubscriptionsOnCommunitiesUseCase: CheckSubscriptionsOnCommunitiesUseCase,
    getCommunitiesUseCase: GetCommunitiesUseCase,
    getRecommendedCommunitiesUseCase: GetRecommendedCommunitiesUseCase
) :
    ModelBase,
    ModelBaseImpl(),
    SubscriptionsModel,
    CheckSubscriptionsOnCommunitiesUseCase by checkSubscriptionsOnCommunitiesUseCase,
    GetCommunitiesUseCase by getCommunitiesUseCase,
    GetRecommendedCommunitiesUseCase by getRecommendedCommunitiesUseCase