package io.golos.cyber_android.ui.screens.community_page

import io.golos.cyber_android.ui.common.mvvm.model.ModelBaseImpl
import io.golos.domain.use_cases.community.GetCommunityPageByIdUseCase
import io.golos.domain.use_cases.community.SubscribeToCommunityUseCase
import io.golos.domain.use_cases.community.UnsubscribeToCommunityUseCase
import javax.inject.Inject

class CommunityPageModelImpl @Inject constructor(
    communityPageByIdUseCase: GetCommunityPageByIdUseCase,
    subscribeToCommunityUseCase: SubscribeToCommunityUseCase,
    unsubscribeToCommunityUseCase: UnsubscribeToCommunityUseCase
) :
    ModelBaseImpl(),
    CommunityPageModel,
    GetCommunityPageByIdUseCase by communityPageByIdUseCase,
    SubscribeToCommunityUseCase by subscribeToCommunityUseCase,
    UnsubscribeToCommunityUseCase by unsubscribeToCommunityUseCase