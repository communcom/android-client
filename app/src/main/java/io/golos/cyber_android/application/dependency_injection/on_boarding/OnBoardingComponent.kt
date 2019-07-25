package io.golos.cyber_android.application.dependency_injection.on_boarding

import dagger.Subcomponent
import io.golos.cyber_android.application.dependency_injection.trending_feed.TrendingFeedModule
import io.golos.cyber_android.application.dependency_injection.trending_feed.TrendingFeedModuleBinds
import io.golos.domain.dependency_injection.scopes.FragmentScope

@Subcomponent(modules = [
    TrendingFeedModule::class,
    TrendingFeedModuleBinds::class
])
@FragmentScope
interface OnBoardingComponent {
}