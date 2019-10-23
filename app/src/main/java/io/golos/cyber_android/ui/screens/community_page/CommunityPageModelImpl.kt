package io.golos.cyber_android.ui.screens.community_page

import io.golos.cyber_android.ui.common.mvvm.model.ModelBaseImpl
import io.golos.domain.interactors.community.GetCommunityPageByIdUseCase
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.flow.asFlow
import javax.inject.Inject

class CommunityPageModelImpl @Inject constructor(communityPageByIdUseCase: GetCommunityPageByIdUseCase) :
    ModelBaseImpl(),
    CommunityPageModel,
    GetCommunityPageByIdUseCase by communityPageByIdUseCase{

}