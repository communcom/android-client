package io.golos.cyber_android.ui.screens.community_page_post.di

import dagger.Module
import dagger.Provides
import io.golos.domain.dependency_injection.Clarification
import javax.inject.Named

@Module
class CommunityPostFragmentModule(private val communityId: String) {

    @Provides
    @Named(value = Clarification.COMMUNITY_ID)
    internal fun provideCommunityId(): String = communityId

}