package io.golos.cyber_android.application.dependency_injection.graph.app.ui.main_activity.communities_fragment.discover_fragment

import dagger.Module
import dagger.Provides
import io.golos.cyber_android.ui.screens.main_activity.communities.data_repository.dto.CommunityType

@Module
class DiscoverFragmentModule {
    @Provides
    internal fun provideCommunityType(): CommunityType = CommunityType.DISCOVERED
}