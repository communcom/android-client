package io.golos.cyber_android.ui.screens.community_page.model

import io.golos.cyber_android.ui.shared.mvvm.model.ModelBase
import io.golos.domain.use_cases.community.BlockUnblockCommunityUseCase
import io.golos.domain.use_cases.community.GetCommunityPageByIdUseCase
import io.golos.domain.use_cases.community.SubscribeToCommunityUseCase
import io.golos.domain.use_cases.community.UnsubscribeToCommunityUseCase

interface CommunityPageModel : ModelBase,
    GetCommunityPageByIdUseCase,
    SubscribeToCommunityUseCase,
    UnsubscribeToCommunityUseCase,
    BlockUnblockCommunityUseCase