package io.golos.cyber_android.locator

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import io.golos.domain.DispatchersProvider
import io.golos.domain.entities.CyberUser
import io.golos.domain.interactors.action.VoteUseCase
import io.golos.domain.interactors.feed.*
import io.golos.domain.interactors.model.CommunityId
import io.golos.domain.interactors.model.DiscussionIdModel
import io.golos.domain.interactors.publish.EmbedsUseCase
import io.golos.domain.interactors.sign.SignInUseCase

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-03-18.
 */
interface ServiceLocator {
    val getAppContext: Context

    val dispatchersProvider: DispatchersProvider

    fun getCommunityFeedViewModelFactory(communityId: CommunityId): ViewModelProvider.Factory

    fun getUserSubscriptionsFeedViewModelFactory(user: CyberUser): ViewModelProvider.Factory

    fun getPostWithCommentsViewModelFactory(postId: DiscussionIdModel): ViewModelProvider.Factory

    fun getCommunityFeedUseCase(communityId: CommunityId): CommunityFeedUseCase

    fun getUserSubscriptionsFeedUseCase(user: CyberUser): UserSubscriptionsFeedUseCase

    fun getUserPostFeedUseCase(user: CyberUser): UserPostFeedUseCase

    fun getVoteUseCase(): VoteUseCase

    fun getCommentsForAPostUseCase(postId: DiscussionIdModel): PostCommentsFeedUseCase

    fun getPostWithCommentsUseCase(postId: DiscussionIdModel): PostWithCommentUseCase

    fun getSignInUseCase(): SignInUseCase

    fun getEmbedsUseCase(): EmbedsUseCase
}