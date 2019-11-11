package io.golos.cyber_android.ui.screens.followers

import io.golos.cyber_android.ui.common.mvvm.model.ModelBaseImpl
import io.golos.domain.use_cases.user.GetFollowersUseCase
import io.golos.domain.use_cases.user.SubscribeToFollowerUseCase
import io.golos.domain.use_cases.user.UnsubscribeToFollowerUseCase
import javax.inject.Inject

class FollowersModelImpl @Inject constructor(
    getFollowersUseCase: GetFollowersUseCase,
    subscribeToFollowerUseCase: SubscribeToFollowerUseCase,
    unsubscribeToFollowerUseCase: UnsubscribeToFollowerUseCase
) : FollowersModel,
    ModelBaseImpl(),
    GetFollowersUseCase by getFollowersUseCase,
    SubscribeToFollowerUseCase by subscribeToFollowerUseCase,
    UnsubscribeToFollowerUseCase by unsubscribeToFollowerUseCase