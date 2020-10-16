package io.golos.cyber_android.ui.screens.community_page_members.di

import dagger.Module
import dagger.Provides
import io.golos.domain.dependency_injection.Clarification
import javax.inject.Named

@Module
class CommunityPageMembersModule(
    private val pageSize: Int
) {
    @Provides
    @Named(Clarification.PAGE_SIZE)
    fun providePageSize(): Int = pageSize
}