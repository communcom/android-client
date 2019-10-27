package io.golos.cyber_android.application.dependency_injection.graph.app.ui.community_page_about

import dagger.Module
import dagger.Provides

@Module
class CommunityPageAboutFragmentModule(private val description: String?) {

    @Provides
    internal fun provideDescription(): String? = description
}