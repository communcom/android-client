package io.golos.cyber_android.application.dependency_injection.graph.app.ui.main_activity.feed_fragment

import dagger.Module
import dagger.Provides
import io.golos.domain.DiscussionsFeedRepository
import io.golos.domain.DispatchersProvider
import io.golos.domain.Repository
import io.golos.domain.dependency_injection.Clarification
import io.golos.domain.dependency_injection.scopes.FragmentScope
import io.golos.domain.entities.CyberUser
import io.golos.domain.entities.FeedRelatedEntities
import io.golos.domain.entities.PostEntity
import io.golos.domain.entities.VoteRequestEntity
import io.golos.domain.interactors.feed.UserSubscriptionsFeedUseCase
import io.golos.domain.interactors.feed.UserSubscriptionsFeedUseCaseImpl
import io.golos.domain.interactors.model.DiscussionsFeed
import io.golos.domain.interactors.model.PostModel
import io.golos.domain.requestmodel.PostFeedUpdateRequest
import io.golos.domain.rules.EntityToModelMapper
import io.golos.sharedmodel.CyberName
import javax.inject.Named

@Module
class MyFeedFragmentModule(private val forUser: CyberUser, private val appUser: CyberName) {
    @Provides
    internal fun provideForUser(): CyberUser = forUser

    @Provides
    internal fun provideAppUser(): CyberName = appUser
}