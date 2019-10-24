package io.golos.cyber_android.ui.screens.community_page

import io.golos.cyber_android.ui.common.mvvm.model.ModelBase
import io.golos.domain.interactors.community.GetCommunityPageByIdUseCase
import io.golos.domain.interactors.community.SubscribeToCommunityUseCase
import io.golos.domain.interactors.community.UnsubscribeToCommunityUseCase

interface CommunityPageModel : ModelBase,
    GetCommunityPageByIdUseCase,
    SubscribeToCommunityUseCase,
    UnsubscribeToCommunityUseCase