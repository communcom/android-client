package io.golos.cyber_android.ui.screens.community_page_about.di

import dagger.Module
import dagger.Provides

@Module
class CommunityPageAboutFragmentModule(private val description: String?) {

    @Provides
    internal fun provideDescription(): String? = description
}