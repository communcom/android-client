package io.golos.cyber_android.ui.screens.communities.community

import io.golos.cyber_android.ui.screens.feed.FeedPageTabViewModel
import io.golos.domain.interactors.action.VoteUseCase
import io.golos.domain.interactors.feed.CommunityFeedUseCase
import io.golos.domain.interactors.publish.DiscussionPosterUseCase
import io.golos.domain.interactors.sign.SignInUseCase
import io.golos.domain.interactors.user.UserMetadataUseCase
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