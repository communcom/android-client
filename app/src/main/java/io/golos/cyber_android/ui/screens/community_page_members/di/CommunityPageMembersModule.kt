package io.golos.cyber_android.ui.screens.community_page_members.di

import dagger.Module
import dagger.Provides
import io.golos.domain.dependency_injection.Clarification
import io.golos.domain.dto.UserIdDomain
import javax.inject.Named

@Module
class CommunityPageMembersModule(
    private val communityId: String,
    private val pageSize: Int
) {
    @Provides
    @Named(Clarification.PAGE_SIZE)
    fun providePageSize(): Int = pageSize

    @Provides
    @Named(Clarification.COMMUNITY_ID)
    fun provideCommunityId(): String = communityId
}