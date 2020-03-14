package io.golos.cyber_android.ui.screens.community_page_post.di

import dagger.Module
import dagger.Provides
import io.golos.domain.dependency_injection.Clarification
import javax.inject.Named

@Module
class CommunityPostFragmentModule(private val communityId: String, private val communityAlias: String?) {

    @Provides
    @Named(value = Clarification.COMMUNITY_CODE)
    internal fun provideCommunityId(): String = communityId

    @Provides
    @Named(value = Clarification.COMMUNITY_ALIAS)
    internal fun provideCommunityAlias(): String? = communityAlias

}