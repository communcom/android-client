package io.golos.cyber_android.ui.screens.community_page_leaders_list.di

import dagger.Module
import dagger.Provides
import io.golos.domain.dependency_injection.Clarification
import javax.inject.Named

@Module
class CommunityPageLeadsListModule(private val communityId: String) {
    @Provides
    @Named(Clarification.COMMUNITY_ID)
    internal fun provideCommunityId(): String = communityId
}