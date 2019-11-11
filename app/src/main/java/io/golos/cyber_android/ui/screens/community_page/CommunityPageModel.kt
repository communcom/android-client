package io.golos.cyber_android.ui.screens.community_page

import io.golos.cyber_android.ui.common.mvvm.model.ModelBase
import io.golos.domain.use_cases.community.GetCommunityPageByIdUseCase
import io.golos.domain.use_cases.community.SubscribeToCommunityUseCase
import io.golos.domain.use_cases.community.UnsubscribeToCommunityUseCase

interface CommunityPageModel : ModelBase,
    GetCommunityPageByIdUseCase,
    SubscribeToCommunityUseCase,
    UnsubscribeToCommunityUseCase