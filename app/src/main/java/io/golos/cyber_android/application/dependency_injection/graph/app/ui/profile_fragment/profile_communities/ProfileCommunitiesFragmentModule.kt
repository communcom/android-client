package io.golos.cyber_android.application.dependency_injection.graph.app.ui.profile_fragment.profile_communities

import dagger.Module
import dagger.Provides
import io.golos.cyber_android.ui.dto.ProfileCommunities

@Module
class ProfileCommunitiesFragmentModule(private val profileCommunities: ProfileCommunities) {
    @Provides
    fun provideProfileCommunities(): ProfileCommunities = profileCommunities
}