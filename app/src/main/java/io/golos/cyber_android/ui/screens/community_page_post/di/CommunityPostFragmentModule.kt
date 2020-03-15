package io.golos.cyber_android.ui.screens.community_page_post.di

import dagger.Module
import dagger.Provides
import io.golos.domain.dependency_injection.Clarification
import io.golos.domain.dto.CommunityIdDomain
import javax.inject.Named

@Module
class CommunityPostFragmentModule(private val communityId: CommunityIdDomain) {
    @Provides
    internal fun provideCommunityId(): CommunityIdDomain = communityId
}