package io.golos.cyber_android.ui.screens.community_page.di

import dagger.Module
import dagger.Provides
import io.golos.domain.dto.CommunityIdDomain

@Module
class CommunityPageFragmentModule(private val communityId: CommunityIdDomain) {
    @Provides
    internal fun provideCommunityId() = communityId
}