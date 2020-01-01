package io.golos.cyber_android.ui.screens.followers

import io.golos.cyber_android.ui.shared.mvvm.model.ModelBase
import io.golos.domain.use_cases.user.GetFollowersUseCase
import io.golos.domain.use_cases.user.SubscribeToFollowerUseCase
import io.golos.domain.use_cases.user.UnsubscribeToFollowerUseCase

interface FollowersModel : ModelBase,
    GetFollowersUseCase,
    SubscribeToFollowerUseCase,
    UnsubscribeToFollowerUseCase