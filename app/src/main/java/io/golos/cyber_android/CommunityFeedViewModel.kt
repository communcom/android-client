package io.golos.cyber_android

import io.golos.domain.interactors.feed.CommunityFeedUseCase
import io.golos.cyber_android.ui.common.posts.AbstractFeedViewModel
import io.golos.cyber_android.ui.screens.feed.FeedPageTabViewModel
import io.golos.domain.model.CommunityFeedUpdateRequest


/**
 * Created by yuri yurivladdurain@gmail.com on 2019-03-18.
 */


class CommunityFeedViewModel(communityFeedUserCase: CommunityFeedUseCase) :
    FeedPageTabViewModel<CommunityFeedUpdateRequest>(communityFeedUserCase)