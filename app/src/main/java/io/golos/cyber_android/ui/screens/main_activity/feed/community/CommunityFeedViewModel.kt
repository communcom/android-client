package io.golos.cyber_android.ui.screens.main_activity.feed.community

import io.golos.cyber_android.ui.screens.main_activity.feed.FeedPageTabViewModel
import io.golos.domain.use_cases.action.VoteUseCase
import io.golos.domain.use_cases.feed.CommunityFeedUseCase
import io.golos.domain.use_cases.publish.DiscussionPosterUseCase
import io.golos.domain.use_cases.sign.SignInUseCase
import io.golos.domain.use_cases.user.UserMetadataUseCase
import io.golos.domain.requestmodel.PostFeedUpdateRequest
import javax.inject.Inject


/**
 * Created by yuri yurivladdurain@gmail.com on 2019-03-18.
 */
class CommunityFeedViewModel
@Inject
constructor (
    communityFeedUserCase: CommunityFeedUseCase,
    voteUseCase: VoteUseCase,
    posterUseCase: DiscussionPosterUseCase,
    userMetadataUseCase: UserMetadataUseCase,
    signInUseCase: SignInUseCase
) : FeedPageTabViewModel<PostFeedUpdateRequest>(communityFeedUserCase, voteUseCase, posterUseCase, signInUseCase, userMetadataUseCase)