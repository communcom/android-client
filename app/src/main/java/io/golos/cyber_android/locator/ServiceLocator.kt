package io.golos.cyber_android.locator

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import com.squareup.moshi.Moshi
import io.golos.cyber4j.model.CyberName
import io.golos.cyber_android.ui.common.helpers.UICalculator
import io.golos.domain.DispatchersProvider
import io.golos.domain.entities.CyberUser
import io.golos.domain.entities.EventTypeEntity
import io.golos.domain.interactors.action.VoteUseCase
import io.golos.domain.interactors.feed.*
import io.golos.domain.interactors.images.ImageUploadUseCase
import io.golos.domain.interactors.model.CommunityId
import io.golos.domain.interactors.model.CommunityModel
import io.golos.domain.interactors.model.DiscussionIdModel
import io.golos.domain.interactors.model.TestPassProvider
import io.golos.domain.interactors.notifs.events.EventsUseCase
import io.golos.domain.interactors.notifs.push.PushNotificationsSettingsUseCase
import io.golos.domain.interactors.publish.DiscussionPosterUseCase
import io.golos.domain.interactors.publish.EmbedsUseCase
import io.golos.domain.interactors.reg.CountriesChooserUseCase
import io.golos.domain.interactors.reg.SignUpUseCase
import io.golos.domain.interactors.settings.SettingsUseCase
import io.golos.domain.interactors.sign.SignInUseCase
import io.golos.domain.interactors.user.UserMetadataUseCase

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-03-18.
 */
interface ServiceLocator {
    val getAppContext: Context

    val dispatchersProvider: DispatchersProvider

    val moshi: Moshi

    fun getDefaultViewModelFactory(): ViewModelProvider.Factory

    fun getUserPostsFeedViewModelFactory(forUser: CyberUser): ViewModelProvider.Factory

    fun getUserSubscriptionsFeedViewModelFactory(forUser: CyberUser, appUser: CyberName): ViewModelProvider.Factory

    fun getViewModelFactoryByCyberName(forUser: CyberName): ViewModelProvider.Factory

    fun getCommunityFeedViewModelFactory(communityId: CommunityId, forUser: CyberName): ViewModelProvider.Factory

    fun getPostWithCommentsViewModelFactory(postId: DiscussionIdModel): ViewModelProvider.Factory

    fun getEditorPageViewModelFactory(
        community: CommunityModel?,
        postToEdit: DiscussionIdModel?
    ): ViewModelProvider.Factory


    fun getCommunityFeedUseCase(communityId: CommunityId): CommunityFeedUseCase

    fun getVoteUseCase(): VoteUseCase

    fun getCommentsForAPostUseCase(postId: DiscussionIdModel): PostCommentsFeedUseCase

    fun getPostWithCommentsUseCase(postId: DiscussionIdModel): PostWithCommentUseCase

    fun getUserSubscriptionsFeedUseCase(user: CyberUser): UserSubscriptionsFeedUseCase

    fun getUserPostFeedUseCase(user: CyberUser): UserPostFeedUseCase

    fun getSignInUseCase(): SignInUseCase

    fun getSignOnUseCase(
        isInTestMode: Boolean,
        testPassProvider: TestPassProvider
    ): SignUpUseCase

    fun getEmbedsUseCase(): EmbedsUseCase

    fun getDiscussionPosterUseCase(): DiscussionPosterUseCase

    fun getCountriesChooserUseCase(): CountriesChooserUseCase

    fun getImageUploadUseCase(): ImageUploadUseCase

    fun getSettingUserCase(): SettingsUseCase

    fun getEventsUseCase(eventTypes: Set<EventTypeEntity>): EventsUseCase

    fun getUserMetadataUseCase(forUser: CyberName): UserMetadataUseCase

    fun getUICalculator(): UICalculator

    fun getPushNotificationsSettingsUseCase(): PushNotificationsSettingsUseCase
}