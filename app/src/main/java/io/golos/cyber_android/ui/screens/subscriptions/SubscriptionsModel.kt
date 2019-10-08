package io.golos.cyber_android.ui.screens.subscriptions

import io.golos.cyber_android.ui.common.mvvm.model.ModelBase
import io.golos.domain.interactors.community.CheckSubscriptionsOnCommunitiesUseCase
import io.golos.domain.interactors.community.GetCommunitiesUseCase
import io.golos.domain.interactors.community.GetRecommendedCommunitiesUseCase

interface SubscriptionsModel : ModelBase,
    CheckSubscriptionsOnCommunitiesUseCase,
    GetCommunitiesUseCase,
    GetRecommendedCommunitiesUseCase