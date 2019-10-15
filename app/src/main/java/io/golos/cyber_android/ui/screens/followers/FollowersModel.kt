package io.golos.cyber_android.ui.screens.followers

import io.golos.cyber_android.ui.common.mvvm.model.ModelBase
import io.golos.domain.interactors.user.GetFollowersUseCase
import io.golos.domain.interactors.user.SubscribeToFollowerUseCase
import io.golos.domain.interactors.user.UnsubscribeToFollowerUseCase

interface FollowersModel : ModelBase,
    GetFollowersUseCase,
    SubscribeToFollowerUseCase,
    UnsubscribeToFollowerUseCase