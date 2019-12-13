package io.golos.cyber_android.ui.screens.profile_posts.di

import dagger.Module
import dagger.Provides
import io.golos.domain.dto.PostsConfigurationDomain

@Module
class ProfilePostsFragmentModule(private val startFeedType: PostsConfigurationDomain.TypeFeedDomain) {
    @Provides
    internal fun provideStartFeedType(): PostsConfigurationDomain.TypeFeedDomain = startFeedType
}
