package io.golos.cyber_android.application.dependency_injection.trending_feed

import dagger.Module
import dagger.Provides
import io.golos.domain.interactors.model.CommunityId
import io.golos.sharedmodel.CyberName

@Module
class TrendingFeedModule(private val communityId: CommunityId, private val forUser: CyberName) {
    @Provides
    internal fun provideCommunityId(): CommunityId = communityId

    @Provides
    internal fun provideForUser(): CyberName = forUser
}