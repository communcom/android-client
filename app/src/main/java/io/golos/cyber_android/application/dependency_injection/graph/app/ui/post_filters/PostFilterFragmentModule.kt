package io.golos.cyber_android.application.dependency_injection.graph.app.ui.post_filters

import dagger.Module
import dagger.Provides
import io.golos.domain.dependency_injection.Clarification
import javax.inject.Named

@Module
class PostFilterFragmentModule(private val isNeedToSaveGlobalState: Boolean) {

    @Provides
    @Named(value = Clarification.FILTER_GLOBAL)
    internal fun provideIsNeedToSaveGlobalState(): Boolean = isNeedToSaveGlobalState

}