package io.golos.cyber_android.ui.screens.communities_list.di

import dagger.Module
import dagger.Provides
import io.golos.domain.dependency_injection.Clarification
import io.golos.domain.dto.UserIdDomain
import javax.inject.Named

@Module
class CommunitiesListFragmentModule(
    private val showBackButton: Boolean,
    private val showToolbar:Boolean,
    private val userId: UserIdDomain,
    private val showAll: Boolean) {

    @Provides
    @Named(Clarification.BACK_BUTTON)
    internal fun provideShowBackButton(): Boolean = showBackButton

    @Provides
    @Named(Clarification.TOOLBAR)
    internal fun provideToolbarVisibility():Boolean = showToolbar

    @Provides
    internal fun provideUserId(): UserIdDomain = userId

    @Provides
    @Named(Clarification.SHOW_ALL)
    internal fun provideShowAll(): Boolean = showAll
}