package io.golos.cyber_android.ui.screens.discovery.view.fragments.discover_five_communities.di

import dagger.Module
import dagger.Provides
import io.golos.domain.dependency_injection.Clarification
import javax.inject.Named

@Module
class DiscoveryFiveCommunitiesFragmentModule(
    private val showBackButton: Boolean,
    private val showToolbar: Boolean,
    private val showAll: Boolean
) {

    @Provides
    @Named(Clarification.BACK_BUTTON)
    internal fun provideShowBackButton(): Boolean = showBackButton

    @Provides
    @Named(Clarification.TOOLBAR)
    internal fun provideToolbarVisibility(): Boolean = showToolbar

    @Provides
    @Named(Clarification.SHOW_ALL)
    internal fun provideShowAll(): Boolean = showAll
}