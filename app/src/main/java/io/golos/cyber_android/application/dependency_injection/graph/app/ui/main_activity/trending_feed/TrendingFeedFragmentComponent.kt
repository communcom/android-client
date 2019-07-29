package io.golos.cyber_android.application.dependency_injection.graph.app.ui.main_activity.trending_feed

import dagger.Subcomponent
import io.golos.domain.dependency_injection.scopes.FragmentScope

@Subcomponent(modules = [
    TrendingFeedFragmentModule::class,
    TrendingFeedFragmentModuleBinds::class
])
@FragmentScope
interface TrendingFeedFragmentComponent {
    @Subcomponent.Builder
    interface Builder {
        fun init(module: TrendingFeedFragmentModule): Builder
        fun build(): TrendingFeedFragmentComponent
    }
}