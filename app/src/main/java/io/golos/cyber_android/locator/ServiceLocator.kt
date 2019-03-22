package io.golos.cyber_android.locator

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import io.golos.domain.entities.CyberUser
import io.golos.domain.interactors.action.VoteUseCase
import io.golos.domain.interactors.feed.CommunityFeedUseCase
import io.golos.domain.interactors.feed.UserPostFeedUseCase
import io.golos.domain.interactors.feed.UserSubscriptionsFeedUseCase
import io.golos.domain.interactors.model.CommunityId

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-03-18.
 */
interface ServiceLocator {
    val getAppContext: Context

    fun getCommunityFeedViewModelFactory(communityId: CommunityId): ViewModelProvider.Factory

    fun getUserSubscriptionsFeedViewModelFactory(user: CyberUser): ViewModelProvider.Factory

    fun getCommunityFeedUseCase(communityId: CommunityId): CommunityFeedUseCase

    fun getUserSubscriptionsFeedUseCase(user: CyberUser): UserSubscriptionsFeedUseCase

    fun getUserPostFeedUseCase(user: CyberUser): UserPostFeedUseCase

    fun getVoteUseCase(): VoteUseCase
}