package io.golos.cyber_android

import io.golos.domain.interactors.feed.CommunityFeedUseCase
import io.golos.cyber_android.ui.screens.feed.FeedPageTabViewModel
import io.golos.domain.interactors.action.VoteUseCase
import io.golos.domain.model.PostFeedUpdateRequest


/**
 * Created by yuri yurivladdurain@gmail.com on 2019-03-18.
 */


class CommunityFeedViewModel(communityFeedUserCase: CommunityFeedUseCase, voteUseCase: VoteUseCase) :
    FeedPageTabViewModel<PostFeedUpdateRequest>(communityFeedUserCase, voteUseCase)