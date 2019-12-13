package io.golos.cyber_android.ui.screens.profile_communities.di

import dagger.Module
import dagger.Provides
import io.golos.cyber_android.ui.dto.ProfileCommunities

@Module
class ProfileCommunitiesFragmentModule(private val profileCommunities: ProfileCommunities) {
    @Provides
    fun provideProfileCommunities(): ProfileCommunities = profileCommunities
}