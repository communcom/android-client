package io.golos.cyber_android.application.dependency_injection.graph.app.ui.main_activity.trending_feed

import dagger.Module
import dagger.Provides
import io.golos.cyber4j.sharedmodel.CyberName
import io.golos.domain.interactors.model.CommunityId

@Module
class TrendingFeedFragmentModule(private val communityId: CommunityId, private val forUser: CyberName) {
    @Provides
    internal fun provideCommunityId(): CommunityId = communityId

    @Provides
    internal fun provideForUser(): CyberName = forUser
}