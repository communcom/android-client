package io.golos.cyber_android.application.dependency_injection.graph.app.ui.main_activity.communities_list_fragment

import dagger.Module
import dagger.Provides
import io.golos.domain.dependency_injection.Clarification
import javax.inject.Named

@Module
class CommunitiesListFragmentModule(private val showBackButton: Boolean) {
    @Provides
    @Named(Clarification.BACK_BUTTON)
    internal fun provideShowBackButton(): Boolean = showBackButton
}