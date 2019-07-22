package io.golos.cyber_android.application.dependency_injection.trending_feed

import dagger.Subcomponent
import io.golos.domain.dependency_injection.scopes.FragmentScope

@Subcomponent(modules = [
    TrendingFeedModule::class,
    TrendingFeedModuleBinds::class
])
@FragmentScope
interface TrendingFeedComponent {
}