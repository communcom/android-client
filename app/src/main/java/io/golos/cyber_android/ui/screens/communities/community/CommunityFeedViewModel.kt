package io.golos.cyber_android.ui.screens.communities.community

import io.golos.cyber_android.ui.screens.feed.FeedPageTabViewModel
import io.golos.domain.interactors.action.VoteUseCase
import io.golos.domain.interactors.feed.CommunityFeedUseCase
import io.golos.domain.interactors.publish.DiscussionPosterUseCase
import io.golos.domain.interactors.user.UserMetadataUseCase
import io.golos.domain.requestmodel.PostFeedUpdateRequest


/**
 * Created by yuri yurivladdurain@gmail.com on 2019-03-18.
 */


class CommunityFeedViewModel(communityFeedUserCase: CommunityFeedUseCase,
                             voteUseCase: VoteUseCase,
                             posterUseCase: DiscussionPosterUseCase,
                             userMetadataUseCase: UserMetadataUseCase) :
    FeedPageTabViewModel<PostFeedUpdateRequest>(communityFeedUserCase, voteUseCase, posterUseCase, userMetadataUseCase)