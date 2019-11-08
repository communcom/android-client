package io.golos.cyber_android.application.dependency_injection.graph.app.ui.my_feed

import dagger.Module
import dagger.Provides
import io.golos.cyber_android.ui.screens.postslist.GetPostsConfiguration

@Module
class MyFeedFragmentModule constructor(private val getPostsConfiguration: GetPostsConfiguration) {

    @Provides
    internal fun provideGetPostsConfiguration(): GetPostsConfiguration = getPostsConfiguration
}